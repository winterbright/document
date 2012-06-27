/*
 * Copyright 2001-2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eredlab.g4.ccl.net.smtp;

import java.util.Enumeration;
import java.util.Vector;

/***
 * A class used to represent forward and reverse relay paths.  The
 * SMTP MAIL command requires a reverse relay path while the SMTP RCPT
 * command requires a forward relay path.  See RFC 821 for more details.
 * In general, you will not have to deal with relay paths.
 * <p>
 * <p>
 * @author Daniel F. Savarese
 * @see SMTPClient
 ***/

public final class RelayPath
{
    Vector _path;
    String _emailAddress;

    /***
     * Create a relay path with the specified email address as the ultimate
     * destination.
     * <p>
     * @param emailAddress The destination email address.
     ***/
    public RelayPath(String emailAddress)
    {
        _path = new Vector();
        _emailAddress = emailAddress;
    }

    /***
     * Add a mail relay host to the relay path.  Hosts are added left to
     * right.  For example, the following will create the path
     * <code><b> &lt @bar.com,@foo.com:foobar@foo.com &gt </b></code>
     * <pre>
     * path = new RelayPath("foobar@foo.com");
     * path.addRelay("bar.com");
     * path.addRelay("foo.com");
     * </pre>
     * <p>
     * @param hostname The host to add to the relay path.
     ***/
    public void addRelay(String hostname)
    {
        _path.addElement(hostname);
    }

    /***
     * Return the properly formatted string representation of the relay path.
     * <p>
     * @return The properly formatted string representation of the relay path.
     ***/
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        Enumeration hosts;

        buffer.append('<');

        hosts = _path.elements();

        if (hosts.hasMoreElements())
        {
            buffer.append('@');
            buffer.append((String)hosts.nextElement());

            while (hosts.hasMoreElements())
            {
                buffer.append(",@");
                buffer.append((String)hosts.nextElement());
            }
            buffer.append(':');
        }

        buffer.append(_emailAddress);
        buffer.append('>');

        return buffer.toString();
    }

}
