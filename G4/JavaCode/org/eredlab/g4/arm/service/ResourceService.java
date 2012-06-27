package org.eredlab.g4.arm.service;

import org.eredlab.g4.bmf.base.BaseService;
import org.eredlab.g4.ccl.datastructure.Dto;

/**
 * 资源模型业务接口
 * 
 * @author XiongChun
 * @since 2010-01-13
 */
public interface ResourceService extends BaseService{
	
	/**
	 * 保存代码表
	 * @param pDto
	 * @return
	 */
	public Dto saveCodeItem(Dto pDto);
	
	/**
	 * 删除代码表
	 * @param pDto
	 * @return
	 */
	public Dto deleteCodeItem(Dto pDto);
	
	/**
	 * 修改代码表
	 * @param pDto
	 * @return
	 */
	public Dto updateCodeItem(Dto pDto);
	
	/**
	 * 保存菜单
	 * @param pDto
	 * @return
	 */
	public  Dto saveMenuItem(Dto pDto);
	
	/**
	 * 删除菜单
	 * @param pDto
	 * @return
	 */
	public Dto deleteMenuItems(Dto pDto);
	
	/**
	 * 修改菜单
	 * @param pDto
	 * @return
	 */
	public Dto updateMenuItem(Dto pDto);
	
}
