/*
 * Copyright © 2015-2016 Commerce Technologies, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.commercehub.gradle.plugin.avro

import static org.gradle.testkit.runner.TaskOutcome.FAILED
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

/**
 * Functional tests related to handling of duplicate type definitions.
 *
 * <p>This situation is generally encountered when schema files define records with inline record/enum definitions, and those inline types
 * are used in more than one file.</p>
 */
class DuplicateHandlingFunctionalSpec extends FunctionalSpec {
    def "Duplicate enum definition succeeds if definition identical"() {
        given:
        copyIdenticalEnum()

        when:
        def result = run()

        then:
        taskInfoAbsent || result.task(":generateAvroJava").outcome == SUCCESS
        taskInfoAbsent || result.task(":compileJava").outcome == SUCCESS
        projectFile("build/classes/main/example/Person.class").file
        projectFile("build/classes/main/example/Cat.class").file
        projectFile("build/classes/main/example/Gender.class").file
    }

    def "Duplicate record definition succeeds if definition identical"() {
        given:
        copyIdenticalRecord()

        when:
        def result = run()

        then:
        taskInfoAbsent || result.task(":generateAvroJava").outcome == SUCCESS
        taskInfoAbsent || result.task(":compileJava").outcome == SUCCESS
        projectFile("build/classes/main/example/Person.class").file
        projectFile("build/classes/main/example/Fish.class").file
        projectFile("build/classes/main/example/Gender.class").file
    }

    def "Duplicate enum definition fails if definition differs"() {
        given:
        copyDifferentEnum()

        when:
        def result = runAndFail()

        then:
        taskInfoAbsent || result.task(":generateAvroJava").outcome == FAILED
        result.output.contains("Found conflicting definition of type example.Gender in "
            + "[src/main/avro/duplicate/Dog.avsc, src/main/avro/duplicate/Person.avsc]")
    }

    def "Duplicate record definition fails if definition differs"() {
        given:
        copyDifferentRecord()

        when:
        def result = runAndFail()

        then:
        taskInfoAbsent || result.task(":generateAvroJava").outcome == FAILED
        result.output.contains("Found conflicting definition of type example.Person in "
            + "[src/main/avro/duplicate/Person.avsc, src/main/avro/duplicate/Spider.avsc]")
    }

    private void copyIdenticalEnum() {
        copyResource("duplicate/Person.avsc", avroDir)
        copyResource("duplicate/Cat.avsc", avroDir)
    }

    private void copyDifferentEnum() {
        copyResource("duplicate/Person.avsc", avroDir)
        copyResource("duplicate/Dog.avsc", avroDir)
    }

    private void copyIdenticalRecord() {
        copyResource("duplicate/Person.avsc", avroDir)
        copyResource("duplicate/Fish.avsc", avroDir)
    }

    private void copyDifferentRecord() {
        copyResource("duplicate/Person.avsc", avroDir)
        copyResource("duplicate/Spider.avsc", avroDir)
    }
}
