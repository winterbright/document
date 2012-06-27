package org.eredlab.g4.demo.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.eredlab.g4.arm.util.idgenerator.IDHelper;
import org.eredlab.g4.bmf.base.BaseServiceImpl;
import org.eredlab.g4.ccl.datastructure.Dto;
import org.eredlab.g4.ccl.datastructure.impl.BaseDto;
import org.eredlab.g4.ccl.util.G4Utils;
import org.eredlab.g4.demo.service.DemoService;
import org.springframework.orm.ibatis.SqlMapClientCallback;

import com.ibatis.sqlmap.client.SqlMapExecutor;

public class DemoServiceImpl extends BaseServiceImpl implements DemoService {

	/**
	 * 插入一条收费项目
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto saveSfxmDomain(Dto pDto) {
		Dto outDto = new BaseDto();
		String xmid = IDHelper.getXmID();
		pDto.put("xmid", xmid);
		g4Dao.insert("Demo.insertEz_sfxmDomain", pDto);
		outDto.put("xmid", xmid);
		return outDto;
	}

	/**
	 * 插入一批收费项目(JDBC批处理演示)
	 * 
	 * @param pDto
	 * @return
	 * @throws SQLException
	 */
	public Dto batchSaveSfxmDomains(final Dto pDto) {
		Dto outDto = new BaseDto();
		g4Dao.getSqlMapClientTpl().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				executor.startBatch();
				for (int i = 0; i < pDto.getAsInteger("count").intValue(); i++) {
					Dto dto = new BaseDto();
					String xmid = IDHelper.getXmID();
					dto.put("xmid", xmid);
					dto.put("sfdlbm", "99");
					executor.insert("Demo.insertEz_sfxmDomain", dto);
				}
				executor.executeBatch();
				return null;
			}
		});
		return outDto;
	}

	/**
	 * 修改一条收费项目
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto updateSfxmDomain(Dto pDto) {
		Dto outDto = new BaseDto();
		g4Dao.update("Demo.updatesfxm", pDto);
		return outDto;
	}

	/**
	 * 删除一条收费项目
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto deleteSfxm(Dto pDto) {
		Dto outDto = new BaseDto();
		g4Dao.delete("Demo.deleteSfxm", pDto);
		return outDto;
	}

	/**
	 * 调用存储过程演示
	 * 
	 * @return
	 */
	public Dto callPrc(Dto inDto) {
		Dto prcDto = new BaseDto();
		prcDto.put("myname", inDto.getAsString("myname"));
		prcDto.put("number1", inDto.getAsBigDecimal("number1"));
		prcDto.put("number2", inDto.getAsBigDecimal("number2"));
		if (G4Utils.defaultJdbcTypeMysql()) {
			g4Dao.callPrc("Demo.g4_prc_demo_mysql", prcDto);
		} else if (G4Utils.defaultJdbcTypeOracle()) {
			g4Dao.callPrc("Demo.g4_prc_demo", prcDto);
		}
		return prcDto;
	}

	/**
	 * 演示声明式事务配置
	 */
	public Dto doTransactionTest() {
		Dto dto = new BaseDto();
		dto.put("sxh", "BJLK1000000000935");
		dto.put("fyze", new BigDecimal(300));
		g4Dao.update("Demo.updateByjsb", dto);
		// 如果在这里制造一个异常,事务将被回滚
		dto.put("fyze", new BigDecimal(300));
		g4Dao.update("Demo.updateByjsb1", dto);
		Dto outDto = (Dto) g4Dao.queryForObject("Demo.queryBalanceInfo", dto);
		return outDto;
	}

	/**
	 * 异常处理
	 */
	public Dto doError() {
		Dto dto = new BaseDto();
		dto.put("sxh", "BJLK1000000000935");
		Dto outDto = (Dto) g4Dao.queryForObject("Demo.queryBalanceInfo1", dto);
		return outDto;
	}

	/**
	 * 保存文件上传数据
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto doUpload(Dto pDto) {
		pDto.put("fileid", IDHelper.getFileID());
		pDto.put("uploaddate", G4Utils.getCurrentTimestamp());
		g4Dao.insert("Demo.insertEa_demo_uploadPo", pDto);
		return null;
	}

	/**
	 * 删除文件数据
	 * 
	 * @param pFileId
	 * @return
	 */
	public Dto delFile(String pFileId) {
		g4Dao.delete("Demo.delFileByFileID", pFileId);
		return null;
	}
}
