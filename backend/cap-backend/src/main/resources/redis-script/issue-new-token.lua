-- Key: Old refresh token
local key = KEYS[1]
-- Input: New refresh token
local newRefreshToken = ARGV[1]
-- Check if the refresh token exists in the Redis
local existingToken = redis.call("GET", key)
-- If the refresh token exists, delete it and save the new refresh token
if existingToken then
    local userId = cjson.decode(existingToken).userId
    local userOauthType = cjson.decode(existingToken).userOauthType
    redis.call("DEL", key)
    newRefreshToken = string.gsub(newRefreshToken, "\"", "")
    redis.call("SET", newRefreshToken, cjson.encode({refreshToken = newRefreshToken, userId = userId, userOauthType = userOauthType}))
    redis.call("EXPIRE", newRefreshToken, 14 * 24 * 60 * 60)
    return userId .. ":" .. userOauthType
else
    -- If the refresh token does not exist, return failure
    return "FAIL"
end