-- 数据库初始化脚本
-- 请先创建数据库：CREATE DATABASE media_share_system CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE media_share_system;

-- 创建网络共享位置表
CREATE TABLE IF NOT EXISTS `mss_network_location` (
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

-- 创建文件信息表
CREATE TABLE IF NOT EXISTS `mss_file` (
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

-- 插入示例网络位置数据
INSERT INTO `mss_network_location` (`ip`, `path`, `user_name`, `pwd`, `create_time`) VALUES
('192.168.95.100', 'shareSpace', 'lel0958_share', 'lel@210958_SH', NOW()),
('192.168.95.100', '学习资料', 'lel0958_share', 'lel@210958_SH', NOW())
ON DUPLICATE KEY UPDATE `create_time` = NOW();

-- 查看创建的表
SHOW TABLES;

-- 查看网络位置数据
SELECT * FROM mss_network_location; 