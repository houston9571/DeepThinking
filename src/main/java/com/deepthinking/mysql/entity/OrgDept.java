package com.deepthinking.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("org_dept")
public class OrgDept extends BaseEntity {


    /**
     * 营业厅代码
     */
    @TableId(value = "dept_code", type = IdType.INPUT)
    private String deptCode;

    /**
     * 营业厅名称
     */
    private String deptName;
    
    private String nameFull;

    private String remark;

}
