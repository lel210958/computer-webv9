-- 数据库迁移脚本
-- 用于从旧表结构迁移到新表结构

USE media_share_system;

-- 1. 备份旧表（如果存在）
-- CREATE TABLE mss_file_backup AS SELECT * FROM mss_file;
-- CREATE TABLE mss_network_location_backup AS SELECT * FROM mss_network_location;

-- 2. 创建新的网络位置表
CREATE TABLE IF NOT EXISTS `mss_network_location_new` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	`ip` VARCHAR(32) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
	`path` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
	`user_name` VARCHAR(128) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
	`pwd` VARCHAR(128) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
	`create_time` DATETIME NOT NULL,
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='网络共享位置'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB;

-- 3. 创建新的文件表
CREATE TABLE IF NOT EXISTS `mss_file_new` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	`file_name` VARCHAR(255) NOT NULL COMMENT '文件名' COLLATE 'utf8mb4_unicode_ci',
	`file_path` VARCHAR(2048) NOT NULL COMMENT '文件路径' COLLATE 'utf8mb4_unicode_ci',
	`parent_path` VARCHAR(2048) NOT NULL COMMENT '上级路径' COLLATE 'utf8mb4_unicode_ci',
	`file_size` BIGINT(20) UNSIGNED NOT NULL DEFAULT '0' COMMENT '文件大小，单位字节',
	`fileType` VARCHAR(32) NOT NULL COMMENT '文件类型' COLLATE 'utf8mb4_unicode_ci',
	`network_location_id` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '网络共享位置id',
	`last_modify` DATETIME NOT NULL COMMENT '最近修改时间',
	`sync_time` DATETIME NOT NULL COMMENT '同步时间',
	`statis_date` DATE NOT NULL COMMENT '分区日期',
	`is_directory` TINYINT(4) UNSIGNED NOT NULL COMMENT '是否是文件夹',
	PRIMARY KEY (`id`) USING BTREE,
	KEY `idx_network_location_id` (`network_location_id`),
	KEY `idx_parent_path` (`parent_path`),
	KEY `idx_file_path` (`file_path`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB;

-- 4. 插入默认网络位置数据
INSERT INTO `mss_network_location_new` (`ip`, `path`, `user_name`, `pwd`, `create_time`) VALUES
('192.168.95.100', 'shareSpace', 'lel0958_share', 'lel@210958_SH', NOW()),
('192.168.95.100', '学习资料', 'lel0958_share', 'lel@210958_SH', NOW())
ON DUPLICATE KEY UPDATE `create_time` = NOW();

-- 5. 迁移文件数据（如果旧表存在）
-- 注意：这里需要根据实际的网络位置ID进行映射
-- INSERT INTO mss_file_new (
--     file_name, file_path, parent_path, file_size, fileType, 
--     network_location_id, last_modify, sync_time, statis_date, is_directory
-- )
-- SELECT 
--     file_name, file_path, parent_path, file_size, fileType,
--     CASE 
--         WHEN network_share_location LIKE '%shareSpace%' THEN 1
--         WHEN network_share_location LIKE '%学习资料%' THEN 2
--         ELSE 1
--     END as network_location_id,
--     last_modify, sync_time, statis_date, is_directory
-- FROM mss_file;

-- 6. 删除旧表（谨慎操作）
-- DROP TABLE IF EXISTS mss_file;
-- DROP TABLE IF EXISTS mss_network_location;

-- 7. 重命名新表
-- RENAME TABLE mss_file_new TO mss_file;
-- RENAME TABLE mss_network_location_new TO mss_network_location;

-- 8. 查看迁移结果
SELECT 'Migration completed. Please verify the data and then uncomment the DROP and RENAME statements above.' as message;

-- 查看新表结构
SHOW TABLES LIKE 'mss_%_new';

-- 查看网络位置数据
SELECT * FROM mss_network_location_new; 