package com.study.springbatch

import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor

class PersonItemProcessor : ItemProcessor<Person, Person> {
    val log = LoggerFactory.getLogger(PersonItemProcessor::class.java)

    override fun process(person: Person): Person? {
        val firstName: String = person.firstName.uppercase()
        val lastName: String = person.lastName.uppercase()

        val transformedPerson: Person = Person(firstName, lastName)

        log.info("Converting ({}) into ({})", person, transformedPerson)
        return transformedPerson
    }
}