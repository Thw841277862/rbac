package com.example.rbac.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author Thw
 * @since 2024-08-18
 */
@Getter
@Setter
@TableName("sys_users_jobs")
public class SysUsersJobs implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId("user_id")
    private Long userId;

    /**
     * 岗位ID
     */
    @TableId("job_id")
    private Long jobId;
}
