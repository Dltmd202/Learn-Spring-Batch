package com.study.springbatch

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import javax.sql.DataSource

@Configuration
class BatchConfiguration {
    val log = LoggerFactory.getLogger(BatchConfiguration::class.java)

    @Bean
    fun reader(): FlatFileItemReader<Person> {
        val flatFileItemReader : FlatFileItemReader<Person> = FlatFileItemReaderBuilder<Person>()
            .name("personItemReader")
            .resource(ClassPathResource("sample-data.csv"))
            .delimited()
            .names("firstName", "lastName")
            .targetType(Person::class.java)
            .build()


        flatFileItemReader.setLineMapper(object : DefaultLineMapper<Person>() {
            init {
                setLineTokenizer(DelimitedLineTokenizer().apply {
                    setNames("firstName", "lastName")
                })
                setFieldSetMapper { fieldSet ->
                    val person = Person()
                    person.firstName = fieldSet.readString("firstName")
                    person.lastName = fieldSet.readString("lastName")
                    log.info("Reading line: {}", fieldSet)
                    person
                }
            }
        })
        return flatFileItemReader
    }

    @Bean
    fun processor() : PersonItemProcessor {
        return PersonItemProcessor()
    }

    @Bean
    fun writer(dataSource: DataSource) : JdbcBatchItemWriter<Person> {
        return JdbcBatchItemWriterBuilder<Person>()
            .sql("insert into people (first_name, last_name) values (:firstName, :lastName)")
            .dataSource(dataSource)
            .beanMapped()
            .build()
    }

    @Bean
    fun step1(
        jobRepository: JobRepository,
        transactionManager: DataSourceTransactionManager,
        reader: FlatFileItemReader<Person>,
        processor: PersonItemProcessor,
        writer: JdbcBatchItemWriter<Person>
    ) : Step{
        return StepBuilder("step1", jobRepository)
            .chunk<Person, Person>(10, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build()
    }


    @Bean
    fun importUserJob(jobRepository: JobRepository, step1: Step, listener: JobCompletionNotificationListener) : Job {
        return JobBuilder("importUserJob", jobRepository)
            .listener(listener)
            .start(step1)
            .build()
    }

}