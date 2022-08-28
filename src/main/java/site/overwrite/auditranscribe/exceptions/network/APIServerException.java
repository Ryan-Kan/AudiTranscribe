/*
 * APIServerException.java
 * Description: Exception to mark when the API server connection encounters a problem, like a
 *              timeout or a refusal to connect.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public Licence as published by the Free Software Foundation, either version 3 of the
 * Licence, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public Licence for more details.
 *
 * You should have received a copy of the GNU General Public Licence along with this program. If
 * not, see <https://www.gnu.org/licenses/>
 *
 * Copyright © AudiTranscribe Team
 */

package site.overwrite.auditranscribe.exceptions.network;

/**
 * Exception to mark when the API server connection encounters a problem, like a timeout or a
 * refusal to connect.
 */
public class APIServerException extends Exception {
    public APIServerException() {
        super();
    }

    public APIServerException(String message) {
        super(message);
    }

    public APIServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public APIServerException(Throwable cause) {
        super(cause);
    }

    protected APIServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
