package nl.han.asd.project.client.commonclient.connection;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import nl.han.asd.project.commonservices.internal.utility.Check;
import nl.han.asd.project.protocol.HanRoutingProtocol;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientLoginResponse;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientRegisterResponse;
import nl.han.asd.project.protocol.HanRoutingProtocol.GraphUpdateResponse;
import nl.han.asd.project.protocol.HanRoutingProtocol.Wrapper;
import nl.han.asd.project.protocol.HanRoutingProtocol.Wrapper.Type;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static nl.han.asd.project.protocol.HanRoutingProtocol.*;

/**
 * Utility class enabling the parsing from
 * {@link Wrapper} objects to {@link GeneratedMessage}.
 *
 * @version 1.0
 */
public class Parser {
    /**
     * Parse the wrapper object to the corresponding
     * {@link GeneratedMessage} implementation.
     *
     * @param wrapper to-be-parsed wrapper
     * @return instance corresponding to the {@link Wrapper#getType()}
     * holding all data contained in the provided wrapper
     * @throws IllegalArgumentException       if wrapper is null
     * @throws InvalidProtocolBufferException if the {@link Wrapper#getData()} field
     *                                        does not contain a valid {@link HanRoutingProtocol} instance
     */
    public static GeneratedMessage parseFrom(Wrapper wrapper) throws InvalidProtocolBufferException {
        Check.notNull(wrapper, "wrapper");

        return ResponseTypes.messageFromWrapper(wrapper);
    }

    /**
     * Parse the message to the corresponding
     * {@link GeneratedMessage} implementation.
     *
     * @param <T>         the type of message to parse
     * @param message     message to be parsed
     * @param targetClass target type of the message
     * @return instance corresponding to the {@link Wrapper#getType()}
     * holding all data contained in the provided wrapper
     * @throws IllegalArgumentException       if wrapper or targetClass is null
     * @throws InvalidProtocolBufferException if the {@link Wrapper#getData()} field
     *                                        does not contain a valid {@link HanRoutingProtocol} instance
     */
    public static <T extends GeneratedMessage> T parseFrom(byte[] message, Class<T> targetClass)
            throws InvalidProtocolBufferException {
        Check.notNull(message, "message");
        Check.notNull(targetClass, "targetClass");

        try {
            return (T) targetClass.getMethod("parseFrom", byte[].class).invoke(null, message);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private enum ResponseTypes {
        REGISTER_RESPONSE(Type.CLIENTREGISTERRESPONSE, ClientRegisterResponse.class),
        CLIENT_LOGIN_RESPONSE(Type.CLIENTLOGINRESPONSE, ClientLoginResponse.class),
        GRAPH_UPDATE_RESPONSE(Type.GRAPHUPDATERESPONSE, GraphUpdateResponse.class),
        CLIENT_RESPONSE(Type.CLIENTRESPONSE, ClientResponse.class),
        MESSAGE(Type.MESSAGE, Message.class),
        MESSAGECONFIRMATION(Type.MESSAGECONFIRMATION, MessageConfirmation.class),
        CLIENTLOGOUTRESPONSE(Type.CLIENTLOGOUTRESPONSE, ClientLogoutResponse.class);

        private static Map<Type, ResponseTypes> map;

        static {
            map = new HashMap<>();

            for (ResponseTypes rt : ResponseTypes.values()) {
                map.put(rt.messageType, rt);
            }
        }

        private Type messageType;
        private Class<? extends GeneratedMessage> messageClass;

        ResponseTypes(Type messageType, Class<? extends GeneratedMessage> messageClass) {
            this.messageType = Check.notNull(messageType, "type");
            this.messageClass = Check.notNull(messageClass, "messageClass");
        }

        private static GeneratedMessage messageFromWrapper(Wrapper wrapper) throws InvalidProtocolBufferException {
            ResponseTypes responseType = map.get(wrapper.getType());

            if (responseType == null) {
                throw new UnsupportedOperationException(wrapper.getType().name());
            }

            try {
                return (GeneratedMessage) responseType.messageClass.getMethod("parseFrom", byte[].class).invoke(null,
                        wrapper.getData().toByteArray());
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                    | NoSuchMethodException | SecurityException e) {
                throw new UnsupportedOperationException(e);
            }
        }
    }
}
