package com.tvz.portal.aop.logging

import io.github.jhipster.config.JHipsterConstants
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles

/**
 * Aspect for logging execution of service and repository Spring components.
 *
 * By default, it only runs with the "dev" profile.
 */
@Suppress("unused")
@Aspect
open class LoggingAspect(private val env: Environment) {

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut(
        "within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)"
    )
    fun springBeanPointcut() =
        Unit // Method is empty as this is just a Pointcut, the implementations are in the advices.

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut(
        "within(com.tvz.portal.repository..*)" +
            " || within(com.tvz.portal.service..*)" +
            " || within(com.tvz.portal.web.rest..*)"
    )
    fun applicationPackagePointcut() =
        Unit // Method is empty as this is just a Pointcut, the implementations are in the advices.

    /**
     * Retrieves the {@link Logger} associated to the given {@link JoinPoint}.
     *
     * @param joinPoint join point we want the logger for.
     * @return {@link Logger} associated to the given {@link JoinPoint}.
     */
    private fun logger(joinPoint: JoinPoint) = LoggerFactory.getLogger(joinPoint.signature.declaringTypeName)

    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint join point for advice.
     * @param e exception.
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    fun logAfterThrowing(joinPoint: JoinPoint, e: Throwable) {
        if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT))) {
            logger(joinPoint).error(
                "Exception in {}() with cause = \'{}\' and exception = \'{}\'",
                joinPoint.signature.name,
                if (e.cause != null) e.cause else "NULL",
                e.message,
                e
            )
        } else {
            logger(joinPoint).error(
                "Exception in {}() with cause = {}",
                joinPoint.signature.name,
                if (e.cause != null) e.cause else "NULL"
            )
        }
    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint join point for advice.
     * @return result.
     * @throws Throwable throws [IllegalArgumentException].
     */
    @Around(value = "applicationPackagePointcut() && springBeanPointcut()")
    fun logAround(joinPoint: ProceedingJoinPoint): Any? {
        val log = logger(joinPoint)
        if (log.isDebugEnabled) {
            log.debug(
                "Enter: ${joinPoint.signature.name}() with argument[s] = ${joinPoint.args.joinToString()}"
            )
        }
        try {
            val result = joinPoint.proceed()
            if (log.isDebugEnabled) {
                log.debug(
                    "Exit: ${joinPoint.signature.name}() with result = $result"
                )
            }
            return result
        } catch (e: IllegalArgumentException) {
            log.error(
                "Illegal argument: ${joinPoint.args.joinToString()} in ${joinPoint.signature.name}()"
            )

            throw e
        }
    }
}
