# Xenia Auth Server API

This API provides endpoints for registration, login, token-based login, user lookup, and friends management for Xenia-compatible services.

---

## Base URL

```
https://network.rushhosting.net:10134/auth
```

---

## Response Format

All endpoints return JSON:

```json
{
  "success": true | false,
  "error": null | <string>,
  "data": <object> | <list> | null
}
```

---

## Endpoints

### 1. Register

**POST** `/auth/register`

**Parameters:**
- `username`: string (required, 4–25 characters)
- `password`: string (required, 4–100 characters)

**Validation:**
- Username: 4–25 chars
- Password: 4–100 chars

**Example:**
```sh
curl -X POST "https://network.rushhosting.net:10134/auth/register?username=alice&password=secret"
```

**Response (success):**
```json
{
  "success": true,
  "error": null,
  "data": {
    "username": "alice",
    "friends": [],
    "offline_xuid": "E000...",
    "online_xuid": "9000...",
    "friendsPrivate": false
  }
}
```

**Error Example:**
```json
{
  "success": false,
  "error": "Missing required fields.",
  "data": null
}
```

---

### 2. Login

**POST** `/auth/login`

**Parameters:**
- `username`: string (required, 4–25 characters)
- `password`: string (required, 4–100 characters)

**Example:**
```sh
curl -X POST "https://network.rushhosting.net:10134/auth/login?username=alice&password=secret"
```

**Response (success):**
```json
{
  "success": true,
  "error": null,
  "data": {
    "token": "<TOKEN>",
    "username": "alice"
  }
}
```

**Error Example:**
```json
{
  "success": false,
  "error": "Password must be 4 characters min and 100 max: 3",
  "data": null
}
```

---

### 3. Token-based Session Login

**POST** `/auth/session`

Allows a user to log in with a previously issued session token.

**Parameters:**
- `token`: Session token (required)

**IP & Username Verification:**  
- The token must have an `ip` and `offlineXuid` claim.
- The requester's IP and username must match the claims in the token.

**Example:**
```sh
curl -X POST "https://network.rushhosting.net:10134/auth/session?token=<TOKEN>"
```

**Response (success):**
```json
{
  "success": true,
  "error": null,
  "data": {
    "token": "<TOKEN>",
    "username": "alice"
  }
}
```

**Error Example:**
```json
{
  "success": false,
  "error": "Unauthorized",
  "data": null
}
```

---

### 4. Get User Info

**GET** `/auth/user/{xuid}`

- `xuid`: Online or offline XUID as a hex string

**Example:**
```sh
curl "https://network.rushhosting.net:10134/auth/user/9000AABBCCDDEEFF"
```

**Response (success):**
```json
{
  "success": true,
  "error": null,
  "data": {
    "username": "alice",
    "friends": ["9000..."],
    "offline_xuid": "E000...",
    "online_xuid": "9000...",
    "friendsPrivate": false
  }
}
```

**Error Example:**
```json
{
  "success": false,
  "error": "Invalid Xuid",
  "data": null
}
```

---

### 5. Get Friends List

**GET** `/auth/user/{xuid}/friends`

- `xuid`: Online or offline XUID as hex string
- **Authorization (optional):** For private friends lists, include token in `Authorization: Bearer <token>` header.

**Example:**
```sh
curl "https://network.rushhosting.net:10134/auth/user/9000AABBCCDDEEFF/friends"
```
or
```sh
curl -H "Authorization: Bearer <TOKEN>" "https://network.rushhosting.net:10134/auth/user/9000AABBCCDDEEFF/friends"
```

**Response (success):**
```json
{
  "success": true,
  "error": null,
  "data": ["9000...", "9000..."]
}
```

**Error Example:**
```json
{
  "success": false,
  "error": "Friends list is private",
  "data": null
}
```

---

## Notes

- **XUID Format:** Hex string (64-bit), e.g. `9000AABBCCDDEEFF`.
- **TOKEN:** Received from `/auth/login`, required for private friends list queries.
- **IP Verification:** `/auth/session` checks your IP matches the one in the token.
- **All endpoints** use JSON responses.
- **Error Handling:** On failure, `"success": false`, and `error` is set. If a user is not found, you’ll get `"error": "User not found"`.

---

## Example Workflow

1. **Register:**  
   `POST /auth/register?username=alice&password=secret`

2. **Login:**  
   `POST /auth/login?username=alice&password=secret`  
   _(Save the returned token)_

3. **Session login:**  
   `POST /auth/session?token=<TOKEN>`

4. **Get user info:**  
   `GET /auth/user/{xuid}`

5. **Get friends list:**  
   `GET /auth/user/{xuid}/friends`  
   _(Add Authorization header if needed)_

---

For questions, open an issue or see the source code in [`wildmaster84/AuthServer`](https://github.com/wildmaster84/AuthServer).