package org.example.policy;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 日志脱敏
 */
@Plugin(name = "DataMaskingRewritePolicy", category = "Core", elementType = "rewritePolicy", printObject = true)
public class DataMaskingRewritePolicy implements RewritePolicy {

    /**
     * 使用静态内部类创建对象，节省空间
     */
    private static class StaticDataMaskingRewritePolicy {
        private static final DataMaskingRewritePolicy DATA_MASKING_REWRITE_POLICY = new DataMaskingRewritePolicy();
    }

    /**
     * 需要加密的字段配置数组
     */
    private static final String[] ENCRYPTION_KEY_ARRAYS = {"userPhone", "legalId", "bankCard", "phoneNumber"};

    /**
     * 将数组转换为集合，方便查找
     */
    private static final List<String> ENCRYPTION_KEYS = new ArrayList<>();

    public DataMaskingRewritePolicy() {
        if (CollectionUtils.isEmpty(ENCRYPTION_KEYS)) {
            ENCRYPTION_KEYS.addAll(Arrays.asList(ENCRYPTION_KEY_ARRAYS));
        }
    }

    /**
     * 日志修改方法，可以对日志进行过滤、修改
     *
     * @param logEvent
     * @return
     */
    @Override
    public LogEvent rewrite(LogEvent logEvent) {
        if (!(logEvent instanceof Log4jLogEvent)) {
            return logEvent;
        }

        Log4jLogEvent log4jLogEvent = (Log4jLogEvent) logEvent;

        Message message = log4jLogEvent.getMessage();
        if (!(message instanceof ParameterizedMessage)) {
            return logEvent;
        }

        ParameterizedMessage parameterizedMessage = (ParameterizedMessage) message;

        Object[] params = parameterizedMessage.getParameters();
        if (params == null || params.length <= 0) {
            return logEvent;
        }
        Object[] newParams = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            try {
                JSONObject jsonObject = JSONUtil.parseObj(params[i].toString());
                // 处理json格式的日志
                newParams[i] = encryption(jsonObject, ENCRYPTION_KEYS);
            } catch (Exception e) {
                newParams[i] = params[i];
            }
        }

        ParameterizedMessage m = new ParameterizedMessage(parameterizedMessage.getFormat(), newParams,
                parameterizedMessage.getThrowable());
        Log4jLogEvent.Builder builder = log4jLogEvent.asBuilder().setMessage(m);
        return builder.build();
    }

    /**
     * 单例模式创建(静态内部类模式)
     *
     * @return
     */
    @PluginFactory
    public static DataMaskingRewritePolicy createPolicy() {
        return StaticDataMaskingRewritePolicy.DATA_MASKING_REWRITE_POLICY;
    }

    /**
     * 处理日志，递归获取值
     * @param object
     * @param encryptionKeys
     * @return
     */
    private Object encryption(Object object, List<String> encryptionKeys) {
        String jsonString = JSONUtil.toJsonStr(object);
        if (object instanceof JSONObject) {
            JSONObject json = JSONUtil.parseObj(jsonString);
            boolean isContain = encryptionKeys.stream().anyMatch(key -> StringUtils.contains(jsonString, key));
            if (isContain) {
                // 判断当前字符串中有没有key值
                Set<String> keys = json.keySet();
                keys.forEach(key -> {
                    boolean result = encryptionKeys.stream().anyMatch(ekey -> Objects.equal(ekey, key));
                    if (result) {
                        String value = json.getStr(key);
                        // 加密
                        json.set(key, desensitize(value));
                    } else {
                        json.set(key, encryption(json.get(key), encryptionKeys));
                    }
                });
            }
            return json;
        } else if (object instanceof JSONArray) {
            JSONArray jsonArray = JSONUtil.parseArray(jsonString);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // 转换
                jsonArray.set(i, encryption(jsonObject, encryptionKeys));
            }
            return jsonArray;
        }
        return object;
    }

    /**
     * 脱敏规则
     * @param val
     * @return
     */
    private String desensitize(String val) {
        if (StringUtils.isEmpty(val)) {
            return val;
        }
        int len = val.length() / 2 + 1;
        String[] arr = new String[len];
        for (int i = 0; i < len; i++) {
            arr[i] = "*";
        }
        int disLen = val.length() - len;
        return StringUtils.left(val, disLen / 2).concat(StringUtils.join(arr)).concat(StringUtils.right(val,
                disLen - (disLen / 2)));
    }

}
