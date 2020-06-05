package com.leewan.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.leewan.aop.Authority;
import com.leewan.aop.OperateAuth;
import com.leewan.bean.SftpOperateBean;
import com.leewan.dao.operate.OperateLogDao;
import com.leewan.util.BaseController;
import com.leewan.util.IOUtils;
import com.leewan.util.R;
import com.leewan.util.SSHUtil;
import com.leewan.util.StringUtils;
import com.leewan.util.UserUtils;
import com.leewan.util.interceptor.PageInfo;

@RestController
@RequestMapping("sftp")
public class SftpController extends BaseController {

	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	OperateLogDao logDao;
	
	@RequestMapping(value="file", method= RequestMethod.POST)
	public Object listFile() throws JSchException {
		JSONObject param = super.getSteamParamters();
		String machineUserId = param.getString("machineUserId");
		String path = param.getString("path");
		if(!StringUtils.hasLength(path)) {
			path = ".";
		}
		ChannelSftp sftp = null;
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			sftp = SSHUtil.openSftp(machineUserId);;
			sftp.cd(path);
			path = sftp.pwd();
			Vector<?> ls = sftp.ls(path);
			for(int i=0;i<ls.size();i++) {
				list.add(SSHUtil.format((LsEntry)ls.get(i)));
			}
		} catch (Exception e) {
			return R.failure().setMsg(e.getMessage());
		} finally {
			IOUtils.close(sftp);
		}
		return R.success().put("path", path).setData(list);
	}
	
	
	@RequestMapping("upload")
    public Object upload(@RequestParam("file") MultipartFile file) throws JSchException {
		if (file.isEmpty()) {
            return R.failure().setMsg("文件内容为空");
        }
		String machineUserId = this.request.getParameter("machineUserId");
		String relativePath = this.request.getParameter("relativePath");
		boolean isFolder = Boolean.valueOf(this.request.getParameter("isFolder"));
		StringBuilder path = new StringBuilder(this.request.getParameter("path"));
		SSHUtil.completePath(path);
		if(isFolder) {
			path.append(relativePath);
		}
        ChannelSftp sftp = null;
        try {
        	sftp = SSHUtil.openSftp(machineUserId);
        	//创建文件夹
        	if(isFolder) {
        		SSHUtil.createDir(sftp, path.toString());
        	}
        	String fileName = file.getOriginalFilename();
        	SSHUtil.completePath(path);
            path.append(fileName);
        	sftp.put(file.getInputStream(), path.toString());
            logger.info("上传成功");
            SftpOperateBean logBean = new SftpOperateBean(path.toString(), SftpOperateBean.OP_TYPE_UPLOAD_FILE, UserUtils.getUser().getId(), machineUserId);
            logDao.saveSftpOperate(logBean);
            return R.success();
        } catch (Exception e) {
        	e.printStackTrace();
        	logger.error(e.toString(), e);
        	return R.failure().setMsg(e.getMessage());
        } finally {
			IOUtils.close(sftp);
		}
    }
	
	@RequestMapping("downLoad")
    public void downLoad() throws JSchException, IOException {
	    String name = request.getParameter("name");
	    StringBuilder path = new StringBuilder(request.getParameter("path"));
	    String machineUserId = request.getParameter("machineUserId");
	    ChannelSftp sftp = SSHUtil.openSftp(machineUserId);
	    
	    SSHUtil.completePath(path);
	    path.append(name);
	    try {
	    	SftpATTRS lstat = sftp.lstat(path.toString());
	    	InputStream inputStream = sftp.get(path.toString());
		    response.setContentType("application/octet-stream");
		    response.setHeader("Content-Disposition","attachment;filename=" + name);
		    System.out.println(inputStream.available());
		    response.setContentLength((int)lstat.getSize());
		    IOUtils.copy(inputStream, response.getOutputStream());
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	    	try {
	    		response.getOutputStream().flush();
		    	response.getOutputStream().close();
			} catch (Exception ex) {
			}
	    	IOUtils.close(sftp);
	    }
    }
	
	
	@RequestMapping("mkdir")
    public Object mkdir() throws JSchException, IOException {
		JSONObject param = super.getSteamParamters();
		String machineUserId = param.getString("machineUserId");
		String name = param.getString("name");
		StringBuilder path = new StringBuilder(param.getString("path"));
		SSHUtil.completePath(path);
		path.append(name);
        ChannelSftp sftp = null;
        try {
        	sftp = SSHUtil.openSftp(machineUserId);
        	SSHUtil.createDir(sftp, path.toString());
            SftpOperateBean logBean = new SftpOperateBean(path.toString(), SftpOperateBean.OP_TYPE_MKDIR, UserUtils.getUser().getId(), machineUserId);
            logDao.saveSftpOperate(logBean);
            return R.success();
        } catch (Exception e) {
        	e.printStackTrace();
        	logger.error(e.toString(), e);
        	return R.failure().setMsg(e.getMessage());
        } finally {
			IOUtils.close(sftp);
		}
    }
	
	
	@RequestMapping(value="delete")
    public Object delete() throws JSchException, IOException {
	    String name = request.getParameter("name");
	    StringBuilder path = new StringBuilder(request.getParameter("path"));
	    String machineUserId = request.getParameter("machineUserId");
	    int type = Integer.parseInt(request.getParameter("type"));
	    SSHUtil.completePath(path);
	    path.append(name);
	    ChannelSftp sftp = null;
	    
	    try {
	    	SftpOperateBean logBean = null;
	    	sftp = SSHUtil.openSftp(machineUserId);
	    	if(type == SSHUtil.FILE_TYPE_FOLDER) {
	    		SSHUtil.rmDir(sftp, path.toString());
	    		logBean = new SftpOperateBean(path.toString(), SftpOperateBean.OP_TYPE_DEL_DIR, UserUtils.getUser().getId(), machineUserId);
	    	}else {
	    		sftp.rm(path.toString());
	    		logBean = new SftpOperateBean(path.toString(), SftpOperateBean.OP_TYPE_DEL_FILE, UserUtils.getUser().getId(), machineUserId);
	    	}
            logDao.saveSftpOperate(logBean);
	    	return R.success();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return R.failure().setMsg(e.getMessage());
	    } finally {
	    	IOUtils.close(sftp);
	    }
    }
	
	
	@RequestMapping(value="sftpLog", method=RequestMethod.GET)
	@OperateAuth(value=Authority.ADMIN_ACTION)
    public Object sftpLog() throws Exception {
		Map<String, Object> paramters = super.getParamters();
		PageInfo page = super.getPageInfo(paramters);
	    List<Map<String, Object>> list = this.logDao.querySftpLog(paramters, page);
	    page.setList(list);
	    return R.success().setData(page);
    }
}
