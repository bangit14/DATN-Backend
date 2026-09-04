package com.backend.matching_service.core.base.paging;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class DataRangeQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long loginUserId;
    private List<Long> loginRoleIds;
    private List<String> loginRoleCodes;
    private Long loginDeptId;
    private Boolean admin;
}