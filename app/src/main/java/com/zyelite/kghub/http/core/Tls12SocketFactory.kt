package com.zyelite.kghub.http.core

import javax.net.ssl.SSLSocketFactory
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import javax.net.ssl.SSLSocket

/**
 * @author ZyElite
 * @create 2018/4/3
 * @description Tls12SocketFactory 解决 Android 4.x 不支持 TLSv1.2 问题
 */
class Tls12SocketFactory(socketFactory: SSLSocketFactory) : SSLSocketFactory() {

    private val TLS_SUPPORT_VERSION = arrayOf("TLSv1.1", "TLSv1.2")

    private var delegate: SSLSocketFactory? = socketFactory


    override fun getDefaultCipherSuites(): Array<String> {
        return delegate!!.defaultCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return delegate!!.supportedCipherSuites
    }

    @Throws(IOException::class)
    override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket {
        return patch(delegate!!.createSocket(s, host, port, autoClose))
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(host: String, port: Int): Socket {
        return patch(delegate!!.createSocket(host, port))
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket {
        return patch(delegate!!.createSocket(host, port, localHost, localPort))
    }

    @Throws(IOException::class)
    override  fun createSocket(host: InetAddress, port: Int): Socket {
        return patch(delegate!!.createSocket(host, port))
    }

    @Throws(IOException::class)
    override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket {
        return patch(delegate!!.createSocket(address, port, localAddress, localPort))
    }

    private fun patch(s: Socket): Socket {
        if (s is SSLSocket) {
            s.enabledProtocols = TLS_SUPPORT_VERSION
        }
        return s
    }
}