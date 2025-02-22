package com.tvz.portal.service

import com.tvz.portal.config.audit.AuditEventConverter
import com.tvz.portal.repository.PersistenceAuditEventRepository
import io.github.jhipster.config.JHipsterProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Optional

/**
 * Service for managing audit events.
 *
 * This is the default implementation to support SpringBoot Actuator `AuditEventRepository`.
 */
@Service
@Transactional
class AuditEventService(
    private val persistenceAuditEventRepository: PersistenceAuditEventRepository,
    private val auditEventConverter: AuditEventConverter,
    private val jHipsterProperties: JHipsterProperties
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Old audit events should be automatically deleted after 30 days.
     *
     * This is scheduled to get fired at 12:00 (am).
     */
    @Scheduled(cron = "0 0 12 * * ?")
    fun removeOldAuditEvents() {
        persistenceAuditEventRepository
            .findByAuditEventDateBefore(Instant.now().minus(jHipsterProperties.auditEvents.retentionPeriod.toLong(), ChronoUnit.DAYS))
            .forEach {
                log.debug("Deleting audit data $it")
                persistenceAuditEventRepository.delete(it)
            }
    }

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<AuditEvent> =
        persistenceAuditEventRepository.findAll(pageable)
            .map { auditEventConverter.convertToAuditEvent(it) }

    @Transactional(readOnly = true)
    fun findByDates(fromDate: Instant, toDate: Instant, pageable: Pageable): Page<AuditEvent> =
        persistenceAuditEventRepository.findAllByAuditEventDateBetween(fromDate, toDate, pageable)
            .map { auditEventConverter.convertToAuditEvent(it) }

    @Transactional(readOnly = true)
    fun find(id: Long): Optional<AuditEvent> =
        persistenceAuditEventRepository.findById(id)
            .map { auditEventConverter.convertToAuditEvent(it) }
}
