package com.study.springbatch

import lombok.Getter
import lombok.Setter

@Getter
@Setter
class Person(var firstName: String = "", var lastName: String = ""){


    override fun toString(): String {
        return "{ firstName=$firstName, lastName=$lastName }"
    }
}