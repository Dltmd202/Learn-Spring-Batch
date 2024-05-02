package com.study.springbatch

import org.slf4j.LoggerFactory
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class JobCompletionNotificationListener(
    private val jdbcTemplate: JdbcTemplate
) : JobExecutionListener {
    val log = LoggerFactory.getLogger(PersonItemProcessor::class.java)

    override fun afterJob(jobExecution: JobExecution) {
        if(jobExecution.status == BatchStatus.COMPLETED){
            log.info("!!! JOB FINISHED! Time to verify the results")

            jdbcTemplate
                .query("select first_name, last_name from people", DataClassRowMapper(Person::class.java))
                .forEach { p -> log.info("Found <{{}}> in the database.", p) }
        }
    }
}