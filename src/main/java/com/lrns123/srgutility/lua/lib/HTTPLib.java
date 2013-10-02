/**
 * Copyright (c) 2013, Lourens "Lrns123" Elzinga
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the author nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.lrns123.srgutility.lua.lib;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class HTTPLib extends TwoArgFunction
{
	private static String userAgent;
	
	private static final int OP_GET = 0;
	private static final int OP_POST = 1;
	private static final int OP_DOWNLOAD = 2;
	private static final int OP_SETUSERAGENT = 3;

	@Override
	public LuaValue call(LuaValue modname, LuaValue env)
	{
		LuaTable t = new LuaTable();
		
		bind(t, HTTPLibV.class, new String[] {"get", "post", "download", "setUserAgent"});

		env.set("HTTP", t);
		env.get("package").get("loaded").set("HTTP", t);

		return t;
	}
	
	public static final class HTTPLibV extends VarArgFunction
	{
		@Override
		public Varargs invoke(Varargs args)
		{
		    switch (opcode)
		    {
		    	case OP_GET:
		    		// HTTP.get(url)
		    		return httpGet(args.arg1().checkjstring());
		    	case OP_POST:
		    		// HTTP.post(url, postArgs, [contentType])
		    		return httpPost(args.arg1().checkjstring(), args.arg(2).checkjstring(), args.arg(3).isnil() ? null : args.arg(3).checkjstring());
		    	case OP_DOWNLOAD:
		    		// HTTP.download(url, destFile)
		    		return download(args.arg1().checkjstring(), args.arg(2).checkjstring());
		    	case OP_SETUSERAGENT:
		    		// HTTP.setUserAgent([agent])
		    		return setUserAgent(args.arg1().isnil() ? null : args.arg1().checkjstring());
		    	
		    }
		    return LuaValue.NIL;
		}
	}
	
	private static LuaValue httpGet(String urlArg)
	{
		try
		{
			URL url = new URL(urlArg);
			
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(15000);
			connection.setReadTimeout(15000);
			
			if (connection instanceof HttpURLConnection)
			{
				if (userAgent != null)
				{
					connection.addRequestProperty("User-Agent", "userAgent");
				}
				connection.setRequestProperty("Cache-Control", "no-cache");
			}
					
			connection.connect();
			
			return LuaValue.valueOf(IOUtils.toString(connection.getInputStream()));
		}
		catch (Exception e)
		{
			throw new LuaError(e);
		}
	}
	
	private static LuaValue httpPost(String urlArg, String postArgsArg, String contentTypeArg)
	{
		try
		{
			URL url = new URL(urlArg);
			String postArgs = postArgsArg;
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			byte[] paramAsBytes = postArgs.getBytes(Charset.forName("UTF-8"));

			connection.setConnectTimeout(15000);
			connection.setReadTimeout(15000);
			connection.setRequestMethod("POST");
			
			if (userAgent != null)
			{
				connection.addRequestProperty("User-Agent", "userAgent");
			}
			
			if (contentTypeArg != null)
			{
				connection.setRequestProperty("Content-Type", contentTypeArg + "; charset=utf-8");
			}

			connection.setRequestProperty("Content-Length", "" + paramAsBytes.length);
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setRequestProperty("Cache-Control", "no-cache");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
			writer.write(paramAsBytes);
			writer.flush();
			writer.close();
			
			return LuaValue.valueOf(IOUtils.toString(connection.getInputStream()));
		}
		catch (Exception e)
		{
			throw new LuaError(e);
		}
	}
	
	private static LuaValue download(String urlArg, String destArg)
	{
		try
		{
			URL url = new URL(urlArg);
			File dest = new File(destArg);
			
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(15000);
			connection.setReadTimeout(15000);
			
			if (connection instanceof HttpURLConnection)
			{
				if (userAgent != null)
				{
					connection.addRequestProperty("User-Agent", "userAgent");
				}
				connection.setRequestProperty("Cache-Control", "no-cache");
			}
					
			connection.connect();
			
			final InputStream dlStream = connection.getInputStream();
			final FileOutputStream outStream = new FileOutputStream(dest);
			
            final byte[] buffer = new byte[24000];
            int readLen;
            while ((readLen = dlStream.read(buffer, 0, buffer.length)) != -1)
            {
                outStream.write(buffer, 0, readLen);
            }
            
            dlStream.close();
            outStream.close();
			
			return LuaValue.NIL;
		}
		catch (Exception e)
		{
			throw new LuaError(e);
		}
	}
	
	private static LuaValue setUserAgent(String agentArg)
	{
		userAgent = agentArg;
		return LuaValue.NIL;
	}
	
}
