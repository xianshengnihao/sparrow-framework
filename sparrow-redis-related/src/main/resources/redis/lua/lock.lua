-- 加锁脚本，其中KEYS[]为外部传入参数
-- KEYS[1]表示key
-- KEYS[2]表示value
-- KEYS[3]表示过期时间
-- pexpire将键key的生存时间设置为ttl毫秒
if redis.call("setNx", KEYS[1], KEYS[2]) == 1 then
 return redis.call("pexpire", KEYS[1], KEYS[3])
end
 return 0