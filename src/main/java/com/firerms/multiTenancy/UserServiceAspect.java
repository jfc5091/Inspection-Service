package com.firerms.multiTenancy;


import com.firerms.service.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserServiceAspect {

    @Before("execution(* com.firerms.service.FireCodeService.*(..))&& target(fireCodeService) ")
    public void aroundExecution(JoinPoint pjp, FireCodeService fireCodeService) {
        org.hibernate.Filter filter = fireCodeService.entityManager.unwrap(Session.class).enableFilter("tenantFilter");
        filter.setParameter("FDID", TenantContext.getCurrentTenant());
        filter.validate();
    }

    @Before("execution(* com.firerms.service.InspectionActionService.*(..))&& target(inspectionActionService) ")
    public void aroundExecution(JoinPoint pjp, InspectionActionService inspectionActionService) {
        org.hibernate.Filter filter = inspectionActionService.entityManager.unwrap(Session.class).enableFilter("tenantFilter");
        filter.setParameter("FDID", TenantContext.getCurrentTenant());
        filter.validate();
    }

    @Before("execution(* com.firerms.service.InspectionChecklistItemService.*(..))&& target(inspectionChecklistItemService) ")
    public void aroundExecution(JoinPoint pjp, InspectionChecklistItemService inspectionChecklistItemService) {
        org.hibernate.Filter filter = inspectionChecklistItemService.entityManager.unwrap(Session.class).enableFilter("tenantFilter");
        filter.setParameter("FDID", TenantContext.getCurrentTenant());
        filter.validate();
    }

    @Before("execution(* com.firerms.service.InspectionChecklistService.*(..))&& target(inspectionChecklistService) ")
    public void aroundExecution(JoinPoint pjp, InspectionChecklistService inspectionChecklistService) {
        org.hibernate.Filter filter = inspectionChecklistService.entityManager.unwrap(Session.class).enableFilter("tenantFilter");
        filter.setParameter("FDID", TenantContext.getCurrentTenant());
        filter.validate();
    }

    @Before("execution(* com.firerms.service.InspectionService.*(..))&& target(inspectionService) ")
    public void aroundExecution(JoinPoint pjp, InspectionService inspectionService) {
        org.hibernate.Filter filter = inspectionService.entityManager.unwrap(Session.class).enableFilter("tenantFilter");
        filter.setParameter("FDID", TenantContext.getCurrentTenant());
        filter.validate();
    }

    @Before("execution(* com.firerms.service.InspectionViolationImageService.*(..))&& target(inspectionViolationImageService) ")
    public void aroundExecution(JoinPoint pjp, InspectionViolationImageService inspectionViolationImageService) {
        org.hibernate.Filter filter = inspectionViolationImageService.entityManager.unwrap(Session.class).enableFilter("tenantFilter");
        filter.setParameter("FDID", TenantContext.getCurrentTenant());
        filter.validate();
    }

    @Before("execution(* com.firerms.service.InspectionViolationService.*(..))&& target(inspectionViolationService) ")
    public void aroundExecution(JoinPoint pjp, InspectionViolationService inspectionViolationService) {
        org.hibernate.Filter filter = inspectionViolationService.entityManager.unwrap(Session.class).enableFilter("tenantFilter");
        filter.setParameter("FDID", TenantContext.getCurrentTenant());
        filter.validate();
    }
}
