package com.blossom.common.cache.pojo;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

@SuppressWarnings("all")
public enum RedisInfoEnum {

    active_defrag_hits         ("[碎片整理] 活动碎片整理进程每分钟执行的值重新分配数。"),
    active_defrag_key_hits     ("[碎片整理] 主动碎片整理的键数"),
    active_defrag_key_misses   ("[碎片整理] 活动碎片整理进程跳过的键数"),
    active_defrag_misses       ("[碎片整理] "),
    active_defrag_running      ("[碎片整理] "),

    rdb_changes_since_last_save("[RDB备份] 自上次转储以来的更改数"),
    rdb_bgsave_in_progress     ("[RDB备份] 是否正在进行RDB保存; 0:否; 1:是;"),
    rdb_last_save_time         ("[RDB备份] 上次创建RDB文件时间"),
    rdb_last_bgsave_status     ("[RDB备份] 上次创建RDB文件的结果, ok:成功"),
    rdb_last_bgsave_time_sec   ("[RDB备份] 上次创建RDB文件用时, 单位:秒"),
    rdb_current_bgsave_time_sec("[RDB备份] 正在创建RDB文件的持续时间"),
    rdb_last_cow_size          ("[RDB备份] 上次创建RDB文件大小, 单位:byte"),
    rdb_last_load_keys_expired ("[RDB备份] 7.0属性"),
    rdb_last_load_keys_loaded  ("[RDB备份] 7.0属性"),

    os                         ("宿主系统版本"),
    arch_bits                  ("[64/32]位"),
    run_id                     ("随机标识符"),
    tcp_port                   ("端口"),
    uptime_in_seconds          ("启动秒数"),
    uptime_in_days             ("启动天数"),
    connected_clients          ("客户端数"),
    blocked_clients            ("等待阻塞命令"),
    redis_mode                 ("Redis模式"),
    role                       ("服务器角色,如果该实例不是任何实例的副本,则值为“master”; 如果该实例是某个主实例的副本,则值为“slave”.; 注意: 一个副本可以是另一个副本的主副本(链式复制)."),
    connected_slaves           ("从服务器数量"),

    used_memory_overhead       ("[内存] 服务器为管理其内部数据结构而分配的所有开销(以字节为单位)的总和"),
    used_memory                ("[内存] Redis分配的内存总量"),
    used_memory_human          ("[内存] Redis分配的内存总量"),
    used_memory_dataset        ("[内存] 数据集的大小, 单位:byte; (used_memory - used_memory_overhead)"),
    used_memory_dataset_perc   ("[内存] used_memory_dataset 占净内存使用量的百分比 (used_memory_dataset / (used_memory - used_memory_startup))"),
    used_memory_peak           ("[内存] Redis内存消耗峰值"),
    used_memory_peak_human     ("[内存] Redis内存消耗峰值"),
    used_memory_peak_perc      ("[内存] 当前内存占峰值的百分比, 即[used_memory/used_memory_peak]"),
    used_memory_rss            ("[内存] 从操作系统的角度，返回Redis已分配的内存总量(俗称常驻集大小), 这个值和[top/ps]等命令的输出一致."),
    used_memory_rss_human      ("[内存] 从操作系统的角度，返回Redis已分配的内存总量(俗称常驻集大小), 这个值和[top/ps]等命令的输出一致."),
    used_memory_lua            ("[内存] Lua脚本占用的内存"),
    used_memory_lua_human      ("[内存] Lua脚本占用的内存"),
    used_memory_scripts        ("[内存] 缓存的Lua脚本使用的内存, 单位:byte"),
    used_memory_scripts_human  ("[内存] 缓存的Lua脚本使用的内存, 单位:byte"),
    used_memory_startup        ("[内存] Redis在启动时消耗的初始内存量, 单位:byte"),

    total_system_memory_human  ("系统总内存"),
    total_commands_processed   ("服务器已执行的命令数"),
    instantaneous_ops_per_sec  ("每秒执行的命令数"),
    expired_keys               ("过期自定删除的键数量"),
    evicted_keys               ("因为容量限制而驱逐的键数量"),
    keyspace_hits              ("命中成功的数量"),
    keyspace_misses            ("命中失败的数量"),

    pubsub_channels("目前被订阅的频道数"),
    ;

    @Getter
    private String desc;

    RedisInfoEnum(String desc) {
        this.desc = desc;
    }

    public static String getDesc(String key) {
        if (StrUtil.isBlank(key)) {
            return "";
        }

        for (RedisInfoEnum value : RedisInfoEnum.values()) {
            if (value.name().equals(key)) {
                return value.desc;
            }
        }

        return "";
    }
}

