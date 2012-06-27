/**
 * 首页部分JS
 * 
 * @author XiongChun
 * @since 2010-03-13
 */
Ext.onReady(function() {
	var themeButton = new Ext.Button({
				text : '主题',
				iconCls : 'themeIcon',
				iconAlign : 'left',
				scale : 'medium',
				width : 50,
				tooltip : '<span style="font-size:12px">切换系统主题样式</span>',
				pressed : true,
				arrowAlign : 'right',
				renderTo : 'themeDiv',
				handler : function() {
					themeWindowInit();
				}
			});

	var mainMenu = new Ext.menu.Menu({
				id : 'mainMenu',
				items : [{
							text : '密码修改',
							iconCls : 'keyIcon',
							handler : function() {
								updateUserInit();
							}
						}, {
							text : '系统锁定',
							iconCls : 'lockIcon',
							handler : function() {
								lockWindow.show();
								setCookie("g4.lockflag", '1', 240);
							}
						}]
			});

	var configButton = new Ext.Button({
				text : '首选项',
				iconCls : 'config2Icon',
				iconAlign : 'left',
				scale : 'medium',
				width : 50,
				tooltip : '<span style="font-size:12px">首选项设置</span>',
				pressed : true,
				renderTo : 'configDiv',
				menu : mainMenu
			});

	var closeButton = new Ext.Button({
				iconCls : 'cancel_48Icon',
				iconAlign : 'left',
				scale : 'medium',
				width : 30,
				tooltip : '<span style="font-size:12px">切换用户,安全退出系统</span>',
				pressed : true,
				arrowAlign : 'right',
				renderTo : 'closeDiv',
				handler : function() {
					window.location.href = 'login.ered?reqCode=logout';
				}
			});

	var root = new Ext.tree.TreeNode({
				text : '根节点',
				id : '00'
			});
	var node01 = new Ext.tree.TreeNode({
				text : '蓝色妖姬',
				theme : 'default',
				id : '01'
			});
	var node02 = new Ext.tree.TreeNode({
				text : '粉红之恋',
				theme : 'lightRed',
				id : '02'
			});
	var node03 = new Ext.tree.TreeNode({
				text : '金碧辉煌',
				theme : 'lightYellow',
				id : '03'
			});
	var node04 = new Ext.tree.TreeNode({
				text : '钢铁战士',
				theme : 'gray',
				id : '04'
			});
	var node05 = new Ext.tree.TreeNode({
				text : '绿水青山',
				theme : 'lightGreen',
				id : '05'
			});
	var node06 = new Ext.tree.TreeNode({
				text : '紫色忧郁',
				theme : 'purple2',
				id : '06'
			});
	root.appendChild(node01);
	root.appendChild(node02);
	root.appendChild(node03);
	root.appendChild(node04);
	root.appendChild(node05);
	root.appendChild(node06);
	var themeTree = new Ext.tree.TreePanel({
				autoHeight : false,
				autoWidth : false,
				autoScroll : false,
				animate : false,
				rootVisible : false,
				border : false,
				containerScroll : true,
				applyTo : 'themeTreeDiv',
				root : root
			});
	themeTree.expandAll();
	themeTree.on('click', function(node) {
				var theme = node.attributes.theme;
				var o = document.getElementById('previewDiv');
				o.innerHTML = '<img src="./resource/image/theme/' + theme
						+ '.jpg" />';
			});

	var previewPanel = new Ext.Panel({
				region : 'center',
				title : '<span class="commoncss">主题预览</span>',
				margins : '3 3 3 0',
				activeTab : 0,
				defaults : {
					autoScroll : true
				},
				contentEl : 'previewDiv'
			});

	var themenav = new Ext.Panel({
		title : '<span class="commoncss">主题列表</span>',
		region : 'west',
		split : true,
		width : 120,
		minSize : 120,
		maxSize : 150,
		collapsible : true,
		margins : '3 0 3 3',
		contentEl : 'themeTreeDiv',
		bbar : [{
					text : '保存',
					iconCls : 'acceptIcon',
					handler : function() {
						if (runMode == '0') {
							Ext.Msg.alert('提示',
									'系统正处于演示模式下运行,您的操作被取消!该模式下只能进行查询操作!');
							return;
						}
						var o = themeTree.getSelectionModel().getSelectedNode();
						saveUserTheme(o);
					}
				}, '->', {
					text : '关闭',
					iconCls : 'deleteIcon',
					handler : function() {
						themeWindow.hide();
					}
				}]
	});

	var themeWindow = new Ext.Window({
				title : '<span class="commoncss">主题设置</span>',
				closable : true,
				width : 500,
				height : 350,
				closeAction : 'hide',
				iconCls : 'theme2Icon',
				collapsible : true,
				titleCollapse : true,
				border : true,
				maximizable : false,
				resizable : false,
				modal : true,
				animCollapse : true,
				animateTarget : Ext.getBody(),
				// border:false,
				plain : true,
				layout : 'border',
				items : [themenav, previewPanel]
			});

	var lockForm = new Ext.form.FormPanel({
		labelWidth : 60,
		defaultType : 'textfield',
		labelAlign : 'right',
		bodyStyle : 'padding:10 5 5 5',
		layout : 'form',
		items : [{
					fieldLabel : '帐户密码',
					name : 'password',
					inputType : 'password',
					id : 'password_lock',
					labelStyle : micolor,
					allowBlank : false,
					maxLength : 50,
					listeners : {
						specialkey : function(field, e) {
							if (e.getKey() == Ext.EventObject.ENTER) {
								unlockSystem();
							}
						}
					},
					anchor : '100%'
				}, {
					xtype : 'panel',
					border : false,
					html : '<div style="font-size:12px;margin-left:10px">(提示:系统已成功锁定,解锁请输入登录帐户密码)</div>'
				}]
	});

	var lockWindow = new Ext.Window({
				title : '<span class="commoncss">系统锁定</span>',
				iconCls : 'lockIcon',
				layout : 'fit',
				width : 320,
				height : 130,
				closeAction : 'hide',
				collapsible : false,
				closable : false,
				maximizable : false,
				border : false,
				modal : true,
				constrain : true,
				animateTarget : Ext.getBody(),
				items : [lockForm],
				listeners : {
					'show' : function(obj) {
						lockForm.form.reset();
						lockForm.findById('password_lock').focus(true, 50);
					}
				},
				buttons : [{
							text : '解锁',
							iconCls : 'keyIcon',
							handler : function() {
								unlockSystem();
							}
						}, {
							text : '重新登录',
							iconCls : 'tbar_synchronizeIcon',
							handler : function() {
								window.location.href = 'login.ered?reqCode=logout';
							}
						}]
			});

	var userFormPanel = new Ext.form.FormPanel({
				defaultType : 'textfield',
				labelAlign : 'right',
				labelWidth : 70,
				frame : false,
				bodyStyle : 'padding:5 5 0',
				items : [{
							fieldLabel : '登录帐户',
							name : 'account',
							id : 'account',
							allowBlank : false,
							readOnly : true,
							fieldClass : 'x-custom-field-disabled',
							anchor : '99%'
						}, {
							fieldLabel : '姓名',
							name : 'username',
							id : 'username',
							allowBlank : false,
							readOnly : true,
							fieldClass : 'x-custom-field-disabled',
							anchor : '99%'
						}, {
							fieldLabel : '当前密码',
							name : 'password2',
							id : 'password2',
							inputType : 'password',
							labelStyle : micolor,
							maxLength : 50,
							allowBlank : false,
							anchor : '99%'
						}, {
							fieldLabel : '新密码',
							name : 'password',
							id : 'password',
							inputType : 'password',
							labelStyle : micolor,
							maxLength : 50,
							allowBlank : false,
							anchor : '99%'
						}, {
							fieldLabel : '确认新密码',
							name : 'password1',
							id : 'password1',
							inputType : 'password',
							labelStyle : micolor,
							maxLength : 50,
							allowBlank : false,
							anchor : '99%'
						}, {
							id : 'userid',
							name : 'userid',
							hidden : true
						}]
			});

	var userWindow = new Ext.Window({
				layout : 'fit',
				width : 300,
				height : 205,
				resizable : false,
				draggable : true,
				closeAction : 'hide',
				modal : true,
				title : '<span class="commoncss">密码修改</span>',
				iconCls : 'keyIcon',
				collapsible : true,
				titleCollapse : true,
				maximizable : false,
				buttonAlign : 'right',
				border : false,
				animCollapse : true,
				animateTarget : Ext.getBody(),
				constrain : true,
				listeners : {
					'show' : function(obj) {
						Ext.getCmp('password2').focus(true,200);
					}
				},
				items : [userFormPanel],
				buttons : [{
					text : '保存',
					iconCls : 'acceptIcon',
					handler : function() {
						if (runMode == '0') {
							Ext.Msg.alert('提示',
									'系统正处于演示模式下运行,您的操作被取消!该模式下只能进行查询操作!');
							return;
						}
						updateUser();
					}
				}, {
					text : '关闭',
					iconCls : 'deleteIcon',
					handler : function() {
						userWindow.hide();
					}
				}]
			});

	function unlockSystem() {
		// showWaitMsg();
		if (!lockForm.form.isValid())
			return;
		var params = lockForm.getForm().getValues();
		Ext.Ajax.request({
					url : 'index.ered?reqCode=unlockSystem',
					success : function(response, opts) {
						var resultArray = Ext.util.JSON
								.decode(response.responseText);
						if (resultArray.flag == "1") {
							lockWindow.hide();
							setCookie("g4.lockflag", '0', 240);
						} else {
							Ext.Msg.alert('提示', '密码错误,请重新输入', function() {
										lockForm.form.reset();
										lockForm.findById('password_lock').focus();
									});
						}
					},
					failure : function(response, opts) {
					},
					params : params
				});
	}

	/**
	 * 主题窗口初始化
	 */
	function themeWindowInit() {
		for (i = 0; i < root.childNodes.length; i++) {
			var child = root.childNodes[i];
			if (default_theme == child.attributes.theme) {
				child.select();
			}
		}
		var o = document.getElementById('previewDiv');
		o.innerHTML = '<img src="./resource/image/theme/' + default_theme
				+ '.jpg" />';
		themeWindow.show();

	}

	/**
	 * 保存用户自定义主题
	 */
	function saveUserTheme(o) {
		showWaitMsg();
		Ext.Ajax.request({
			url : './index.ered?reqCode=saveUserTheme',
			success : function(response) {
				var resultArray = Ext.util.JSON.decode(response.responseText);
				Ext.MessageBox
						.confirm(
								'请确认',
								'您选择的['
										+ o.text
										+ ']主题保存成功,立即应用该主题吗?<br>提示：页面会被刷新,请先确认是否有尚未保存的业务数据,以免丢失!',
								function(btn, text) {
									if (btn == 'yes') {
										showWaitMsg('正在为您应用主题...');
										location.reload();
									} else {
										Ext.Msg.alert('提示',
												'请在任何时候按[F5]键刷新页面或者重新登录系统以启用['
														+ o.text + ']主题!',
												function() {
													themeWindow.hide();
												});

									}
								});
			},
			failure : function(response) {
				var resultArray = Ext.util.JSON.decode(response.responseText);
				Ext.Msg.alert('提示', '数据保存失败');
			},
			params : {
				theme : o.attributes.theme
			}
		});
	}

	/**
	 * 加载当前登录用户信息
	 */
	function updateUserInit() {
		userFormPanel.form.reset();
		userWindow.show();
		userWindow.on('show', function() {
					setTimeout(function() {
								userFormPanel.form.load({
											waitTitle : '提示',
											waitMsg : '正在读取用户信息,请稍候...',
											url : 'index.ered?reqCode=loadUserInfo',
											success : function(form, action) {
											},
											failure : function(form, action) {
												Ext.Msg
														.alert(
																'提示',
																'数据读取失败:'
																		+ action.failureType);
											}
										});
							}, 5);
				});
	}

	/**
	 * 修改用户信息
	 */
	function updateUser() {
		if (!userFormPanel.form.isValid()) {
			return;
		}
		password1 = Ext.getCmp('password1').getValue();
		password = Ext.getCmp('password').getValue();
		if (password1 != password) {
			Ext.Msg.alert('提示', '两次输入的密码不匹配,请重新输入!');
			Ext.getCmp('password').setValue('');
			Ext.getCmp('password1').setValue('');
			return;
		}
		userFormPanel.form.submit({
					url : 'index.ered?reqCode=updateUserInfo',
					waitTitle : '提示',
					method : 'POST',
					waitMsg : '正在处理数据,请稍候...',
					success : function(form, action) {
						Ext.MessageBox.alert('提示', '密码修改成功', function() {
									userWindow.hide();
								});
					},
					failure : function(form, action) {
						var flag = action.result.flag;
						if (flag == '0') {
							Ext.MessageBox.alert('提示', '您输入的当前密码验证失败,请重新输入',
									function() {
										Ext.getCmp('password2').setValue('');
										Ext.getCmp('password2').focus();
									});
						} else {
							Ext.MessageBox.alert('提示', '密码修改失败');
						}
					}
				});
	}

	if (getCookie("g4.lockflag") == '1') {
		lockWindow.show();
	}

});

/**
 * 显示系统时钟
 */
function showTime() {
	var date = new Date();
	var h = date.getHours();
	h = h < 10 ? '0' + h : h;
	var m = date.getMinutes();
	m = m < 10 ? '0' + m : m;
	var s = date.getSeconds();
	s = s < 10 ? '0' + s : s;
	document.getElementById('rTime').innerHTML = h + ":" + m + ":" + s;
}

window.onload = function() {
	setInterval("showTime()", 1000);

}