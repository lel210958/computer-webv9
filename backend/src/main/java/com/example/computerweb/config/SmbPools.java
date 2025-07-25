package com.example.computerweb.config;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SmbPools {

    // 控制器新增字段
    private final Map<String, GenericObjectPool<Session>> smbPools = new ConcurrentHashMap<>();

    public GenericObjectPool<Session> getOrCreatePool(String host, String user, String pwd) {
        return smbPools.computeIfAbsent(host + user, k -> new GenericObjectPool<>(new SmbPools.SmbSessionPool(host, user, pwd)));
    }

    public static class SmbSessionPool extends BasePooledObjectFactory<Session> {
        private final SMBClient client = new SMBClient();
        private final String host, user, password;

        public SmbSessionPool(String host, String user, String password) {
            this.host = host;
            this.user = user;
            // 确保密码不为null
            this.password = password != null ? password : "";
        }

        @Override
        public Session create() throws Exception {
            Connection conn = client.connect(host);
            // 确保密码不为null，如果为空字符串则使用null进行匿名认证
            String safePassword = password != null && !password.trim().isEmpty() ? password : null;
            char[] passwordChars = safePassword != null ? safePassword.toCharArray() : null;
            return conn.authenticate(new AuthenticationContext(user, passwordChars, ""));
        }

        @Override
        public PooledObject<Session> wrap(Session session) {
            return new DefaultPooledObject<>(session);
        }
    }
}

