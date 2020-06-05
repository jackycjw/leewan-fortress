$(function()
{
	var app = new Vue(
	{
		el: '#loginApp',
		data:
		{
			btnName:"登录",
			loading: false,
			code:"",
			password: ""
		},
		created: function()
		{
		},
		methods:
		{
			login: function()
			{
				this.loading = true;
				var url = getContextPath() + "/user/login";
				var param =
				{
					code: this.code,
					password: this.password
				};
				var vueThis = this;
				post(url,param, function(res)
				{
					try
					{
						if(res.code == "1000")
						{
							localStorage.setItem("chenjwToken",res.data)
							window.location.href= getContextPath() + "/page/main";
						}
						else
						{
							vueThis.$message.error(res.data);
						}
					}
					catch (e)
					{
						// TODO: handle exception
					}
					finally
					{
						vueThis.loading = false;
					}
				});
			},
			toRegisterPage: function(){
				window.location.href = "/page/page/register";
			}
		}
	})
})
