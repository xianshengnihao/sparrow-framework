-- 解锁脚本
-- KEYS[1]表示key
-- KEYS[2]表示value
if redis.call("get",KEYS[1]) == KEYS[2] then
 return redis.call("del",KEYS[1])
end
 return -1