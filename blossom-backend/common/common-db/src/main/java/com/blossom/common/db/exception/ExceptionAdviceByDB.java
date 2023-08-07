package com.blossom.common.db.exception;

import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.BaseProperties;
import com.blossom.common.base.exception.AbstractExceptionAdvice;
import com.blossom.common.base.pojo.R;
import com.blossom.common.base.pojo.RCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

/**
 * 全局异常处理器
 * <p>
 * 统一以 R 对象格式返回给客服端
 *
 * @author xzzz
 * @since 0.0.1
 */
@Slf4j
@Order(-1)
@RestControllerAdvice
public class ExceptionAdviceByDB extends AbstractExceptionAdvice {

	public ExceptionAdviceByDB(BaseProperties baseProperties) {
		super(baseProperties);
	}

	/**
	 * 没有填写必填项
	 * 字段过长
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public R<?> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
		MsgTarget[] targets = {
				new MsgTarget("Cause: java.sql.SQLException: Field '", "字段 [%s] 为必填项"),
				new MsgTarget("Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column '", "[%s] 超出最大长度")
		};
		String msg = getMsg(exception, targets);
		printExLog(exception, msg, true);
		return R.fault(RCode.BAD_REQUEST.getCode(), msg, exception.getMessage());
	}

	/**
	 * 数据库唯一键重复
	 */
	@ExceptionHandler(DuplicateKeyException.class)
	public R<?> handleDuplicateKeyException(DuplicateKeyException exception) {
		MsgTarget[] targets = {
				new MsgTarget("Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry '", "唯一键 [%s] 重复")
		};
		String msg = getMsg(exception, targets);
		printExLog(exception, msg, true);
		return R.fault(RCode.BAD_REQUEST.getCode(), msg, exception.getMessage());
	}


	/**
	 * SQL 语句执行有错误
	 */
	@ExceptionHandler(BadSqlGrammarException.class)
	public R<?> handleSQLSyntaxErrorException(BadSqlGrammarException exception) {
		MsgTarget[] targets = {
				new MsgTarget("SQLSyntaxErrorException: Unknown column '", "语句中发现未知字段 [%s]"),
		};
		String msg = getMsg(exception, targets);
		printExLog(exception, msg, true);
		return R.fault(RCode.INTERNAL_SQL_ERROR.getCode(),
				RCode.INTERNAL_SQL_ERROR.getMsg() +
						String.format("[%s-%s], %s", exception.getSQLException().getErrorCode(), exception.getSQLException().getSQLState(),msg),
				exception.getSql() + ">>" + exception.getMessage());
	}

	/**
	 * mybatis 中出现的错误
	 */
	@ExceptionHandler(MyBatisSystemException.class)
	public R<?> handleMyBatisSystemException(MyBatisSystemException exception) {
		MsgTarget[] targets = {
				new MsgTarget("There is no getter for property named '",
						"未在 Mapper.xml 的请求或响应 Entity 中找到字段 [%s] 的 getter 方法, 请检查 Entity 与 XML 的字段映射关系."),
		};
		String msg = getMsg(exception, targets);
		printExLog(exception, msg, true);
		return R.fault(RCode.INTERNAL_SQL_ERROR.getCode(), msg, exception.getMessage());
	}

	private String getMsg(Throwable exception, MsgTarget ... targets) {
		String exMsg = Optional.ofNullable(exception.getMessage()).orElse("");
		if (targets == null) {
			return exMsg;
		}
		for (MsgTarget msgTarget : targets) {
			// 如果异常包含该内容
			if (exMsg.contains(msgTarget.getTarget())) {
				String entry = exMsg.substring(exMsg.indexOf(msgTarget.getTarget()) + msgTarget.getTarget().length());
				entry = entry.substring(0, entry.indexOf("'"));
				if (StrUtil.isNotBlank(entry)) {
					return String.format(msgTarget.getErrMsg(), entry);
				}
			}
		}
		exception.printStackTrace();
		return exMsg;
	}

	@Data
	static class MsgTarget {
		/** 异常内容 */
		private String target;
		private String errMsg;

		public MsgTarget(String target, String errMsg) {
			this.target = target;
			this.errMsg = errMsg;
		}
	}
}