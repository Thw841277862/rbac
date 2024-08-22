package com.example.rbac.common;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.sql.SQLException;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理类
 *
 * @author thw
 * @date 2024/6/24 15:19
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {


    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public Result handleException(MethodArgumentTypeMismatchException e) {
        Throwable cause = e.getCause();
        if (cause instanceof ConversionFailedException) {
            Throwable cause2 = cause.getCause();
            if (cause2 instanceof DateTimeParseException) {
                log.error("时间格式输入错误:{}", cause2.getMessage(), cause2);
                return ResultTools.of(CommonStatus.MISSING_PATH_VARIABLE.getBusiCode(), "时间格式输入错误");
            }
        } else {
            log.error("参数类型不匹配或参数类型转换错误:{}", e.getMessage(), e);
        }
        return ResultTools.of(CommonStatus.MISSING_PATH_VARIABLE.getBusiCode(), "参数类型不匹配或参数类型转换错误");
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public Result handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String paramName = e.getParameterName();
        String paramType = e.getParameterType();
        String message = e.getMessage();
        log.error("缺少请求参数,参数名:{}, 参数类型:{}, 详细描述：{}", new Object[]{paramName, paramType, message, e});
        String retmsg = "缺少" + paramName + "参数";
        return ResultTools.of(CommonStatus.MISSING_SERVLET_REQUEST_PARAMETER.getBusiCode(), retmsg);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({MissingPathVariableException.class})
    public Result missingPathVariableException(MissingPathVariableException e) {
        log.error("缺少请求参数或请求路径错误:{}", e.getMessage(), e);
        return ResultTools.of(CommonStatus.MISSING_PATH_VARIABLE.getBusiCode(), CommonStatus.MISSING_PATH_VARIABLE.getMsg());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public Result handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("参数解析异常:{}", e.getMessage(), e);
        return ResultTools.of(CommonStatus.HTTP_MESSAGE_NOT_READABLE.getBusiCode(), CommonStatus.HTTP_MESSAGE_NOT_READABLE.getMsg());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        FieldError error = bindingResult.getFieldError();
        String field = error.getField();
        String showMessage = field + "：" + error.getDefaultMessage();
        log.error("{}参数验证不通过,错误描述：{}", field, showMessage);
        return ResultTools.of(CommonStatus.METHOD_ARGUMENT_NOT_VALID.getBusiCode(), showMessage);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({ConstraintViolationException.class})
    public Result handleServiceException(ConstraintViolationException e) {
        log.error("参数验证失败: {}", e.getMessage(), e);
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = (ConstraintViolation) violations.iterator().next();
        String message = "parameter:" + violation.getMessage();
        return ResultTools.of(CommonStatus.CONSTRAINT_VIOLATION.getBusiCode(), message);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({ValidationException.class})
    public Result handleValidationException(ValidationException e) {
        log.error("参数验证失败, 内部实体字段映射错误:{}", e.getMessage(), e);
        return ResultTools.of(CommonStatus.VALIDATION.getBusiCode(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({BindException.class})
    public Result handleBindException(BindException e) {
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();
        String field = error.getField();
        String defaultMessage = error.getDefaultMessage();
        log.error("参数{}绑定失败,描述:{}", new Object[]{field, defaultMessage, e});
        return ResultTools.of(CommonStatus.BIND.getBusiCode(), defaultMessage);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({MismatchedInputException.class})
    public Result handleMissingServletRequestParameterException(MismatchedInputException e) {
        List<String> path = (List) e.getPath().stream().map(JsonMappingException.Reference::getFieldName).collect(Collectors.toList());
        log.error("缺少【{}】参数, 异常信息:【{}】", new Object[]{path, e.getMessage(), e});
        return ResultTools.of(400, "缺少【" + path + "】参数");
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public Result handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String[] supportMethods = e.getSupportedMethods();
        log.error("请求方法仅限于：{}", supportMethods);
        return ResultTools.of(CommonStatus.HTTP_REQUEST_METHOD_NOT_SUPPORTED.getBusiCode(), "请求方法仅限于:" + JSONUtil.toJsonStr(supportMethods));
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})

    public Result handleHttpMediaTypeNotSupportedException(Exception e) {
        log.error("不支持当前媒体类型: {}", e.getMessage(), e);
        return ResultTools.of(CommonStatus.HTTP_MEDIA_TYPE_NOT_SUPPORTED.getBusiCode(), CommonStatus.HTTP_MEDIA_TYPE_NOT_SUPPORTED.getMsg());
    }


    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({ValidateException.class})
    public Result handleValidationException(ValidateException e) {
        log.error("参数验证失败, 内部实体字段映射错误:{}", e);
        return ResultTools.of(CommonStatus.VALIDATION.getBusiCode(), e.getMessage());
    }


    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({RuntimeException.class})
    public Result handleException(RuntimeException e) {
        log.error("接口RuntimeException异常:{}", e.getMessage(), e);
        if (e.getMessage().equals("Too many requests")) {
            return ResultTools.of(HttpStatus.TOO_MANY_REQUESTS.value(), HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
        }
        return ResultTools.of(CommonStatus.FAIL.getBusiCode(), CommonStatus.FAIL.getMsg());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({Exception.class})
    public Result handleException(Exception e) {
        log.error("接口未知异常: {}", e.getMessage(), e);
        return ResultTools.of(CommonStatus.FAIL.getBusiCode(), CommonStatus.FAIL.getMsg());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({IllegalArgumentException.class})
    public Result handleException(IllegalArgumentException e) {
        log.error("非法的参数错误: {}", e.getMessage(), e);
        return ResultTools.of(CommonStatus.INVALID_PARAM.getBusiCode(), CommonStatus.INVALID_PARAM.getMsg());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({BaseException.class})
    public Result handleException(BaseException e) {
        log.error("业务逻辑错误: code:{},msg:{}", e.getCode(), e.getMsg());
        return ResultTools.of(e.getCode(), e.getMsg());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(SQLException.class)
    public Result handlerSqlException(SQLException e) {
        log.error(e.getMessage(), e);
        return Result.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result handlerNoFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return Result.error(404, "路径不存在，请检查路径是否正确");
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return Result.error(300, "数据库中已存在该记录");
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result handleMaxSizeException(MaxUploadSizeExceededException exc) {
        log.error(exc.getMessage(), exc);
        return Result.error(413, "File size exceeds the maximum limit of 100MB!");
    }

}
