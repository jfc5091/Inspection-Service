package com.firerms.multiTenancy.auditLogs.respository;

import com.firerms.multiTenancy.auditLogs.entity.AuditLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogsRepository extends JpaRepository<AuditLogs, Long> {

}
