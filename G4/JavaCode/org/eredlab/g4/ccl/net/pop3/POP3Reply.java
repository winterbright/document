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
package org.eredlab.g4.ccl.net.pop3;

/***
 * POP3Reply stores POP3 reply code constants.
 * <p>
 * <p>
 * @author Daniel F. Savarese
 ***/

public final class POP3Reply
{
    /*** The reply code indicating success of an operation. ***/
    public static int OK = 0;

    /*** The reply code indicating failure of an operation. ***/
    public static int ERROR = 1;

    // Cannot be instantiated.
    private POP3Reply()
    {}
}
