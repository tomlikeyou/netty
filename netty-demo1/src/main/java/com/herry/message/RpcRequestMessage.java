package com.herry.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class RpcRequestMessage extends Message {
    /**
     * 调用的接口全路径名，服务端根据他找到实现
     */
    private String interfaceName;
    /**
     * 调用接口里面的方法名
     */
    private String methodName;
    /**
     * 方法返回类型
     */
    private Class<?> returnType;
    /**
     * 方法参数类型数组
     */
    private Class[] parameterTypes;
    /**
     * 方法参数值数组
     */
    private Object[] parameterValues;

    public RpcRequestMessage(int sequenceId, String interfaceName, String methodName, Class<?> returnType,
                             Class[] parameterTypes, Object[] parameterValues) {
        super.setSequenceId(sequenceId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValues = parameterValues;
    }

    @Override
    public int getMessageType() {
        return RPC_Message_TYPE_REQUEST;
    }
}
