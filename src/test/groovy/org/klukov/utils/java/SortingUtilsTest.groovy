package org.klukov.utils.java

import spock.lang.Specification

class SortingUtilsTest extends Specification {

    def "test when elements are empty"() {
        given:
        def elements = []
        def keys = ["Alice", "Bob", "Charlie"]
        def keyExtractor = { Person p -> p.name() }

        when:
        def result = SortingUtils.sort(elements, keys, keyExtractor)

        then:
        result == []
    }

    def "test when keys are empty"() {
        given:
        def person1 = new Person("Alice", "Adams", 30)
        def person2 = new Person("Bob", "Brown", 25)
        def person3 = new Person("Charlie", "Clark", 35)

        def elements = [person1, person2, person3]
        def keys = []
        def keyExtractor = { Person p -> p.name() }

        when:
        def result = SortingUtils.sort(elements, keys, keyExtractor)

        then:
        result == elements
    }

    def "test sort with elements and keys in 1:1 relationship"() {
        given:
        def person1 = new Person("Alice","Adams", 30)
        def person2 = new Person("Bob", "Brown", 25)
        def person3 = new Person("Charlie", "Clark", 35)

        def elements = [person3, person1, person2]
        def keys = ["Alice", "Bob", "Charlie"]
        def keyExtractor = { Person p -> p.name() }

        when:
        def result = SortingUtils.sort(elements, keys, keyExtractor)

        then:
        result == [person1, person2, person3]
    }

    def "test when keys are empty, but the key comparator exist"() {
        given:
        def person1 = new Person("Alice", "Adams", 30)
        def person2 = new Person("Bob", "Brown", 25)
        def person3 = new Person("Charlie", "Clark", 35)

        // Intentionally not in order by lastName
        def elements = [person3, person1, person2]
        def keys = []
        def keyExtractor = { Person p -> p.name() }
        def sameKeyComparator = Comparator.comparing({ Person p -> p.lastName() })

        when:
        def result = SortingUtils.sort(elements, keys, keyExtractor, sameKeyComparator)

        then:
        result == [person1, person2, person3]
    }

    def "test if null keys are sorted as given in keys collection"() {
        given:
        def person1 = new Person("Alice", "Adams", 30)
        def person2 = new Person(null, "Brown", 25)
        def person3 = new Person(null, "Clark", 28)
        def person4 = new Person("David",  "Davis", 35)
        def person5 = new Person("Eve",  "Evans",40)

        def elements = [person2, person4, person5, person1, person3]
        def keys = ["Alice", null, "Charlie", "David"]
        def keyExtractor = { Person p -> p.name() }

        when:
        def result = SortingUtils.sort(elements, keys, keyExtractor)

        then:
        result == [person1, person2, person3, person4, person5]
    }

    def "test the same key comparator"() {
        given:
        def person1 = new Person("Alice", "Adams", 30)
        def person2 = new Person("Alice", "Brown", 25)
        def person3 = new Person("Alice", "Clark", 35)
        def person4 = new Person("Bob", "Davis", 40)
        def person5 = new Person("Bob", "Evans", 28)

        def elements = [person5, person3, person1, person4, person2]
        def keys = ["Alice", "Bob"]
        def keyExtractor = { Person p -> p.name() }
        def sameKeyComparator = Comparator.comparing({ Person p -> p.lastName() })

        when:
        def result = SortingUtils.sort(elements, keys, keyExtractor, sameKeyComparator)

        then:
        result == [person1, person2, person3, person4, person5]
    }

    def "test the same order as input when the same key comparator gives the same result"() {
        given:
        def person1 = new Person("Alice", "Adams", 30)
        def person2 = new Person("Alice", "Adams", 25)
        def person3 = new Person("Alice", "Adams", 35)
        def person4 = new Person("Bob", "Brown", 40)
        def person5 = new Person("Bob","Brown", 28)

        def elements = [person1, person4, person5, person2, person3]
        def keys = ["Alice", "Bob"]
        def keyExtractor = { Person p -> p.name() }
        def sameKeyComparator = Comparator.comparing({ Person p -> p.lastName() })

        when:
        def result = SortingUtils.sort(elements, keys, keyExtractor, sameKeyComparator)

        then:
        result == [person1, person2, person3, person4, person5]
    }

    def "test not given elements regarding keys if they are at the end"() {
        given:
        def person1 = new Person("Alice", "Adams", 30)
        def person2 = new Person("Bob", "Brown", 25)
        def person3 = new Person("Charlie", "Clark", 35)
        def person4 = new Person("David", "Davis", 40)
        def person5 = new Person("Eve", "Evans", 28)

        def elements = [person3, person2, person4, person1, person5]
        def keys = ["unknown1", "Alice", "unknown2", "Bob", "unknown3"]
        def keyExtractor = { Person p -> p.name() }

        when:
        def result = SortingUtils.sort(elements, keys, keyExtractor)

        then:
        result == [person1, person2, person3, person4, person5]
    }

    def "test null objects are at the end"() {
        given:
        def person1 = new Person("Alice", "Adams", 30)
        def person2 = new Person("Bob", "Brown", 25)

        def elements = [ null, person1, null, person2]
        def keys = []
        def keyExtractor = { Person p -> p.name() }

        when:
        def result = SortingUtils.sort(elements, keys, keyExtractor)

        then:
        result == [person1, person2, null, null]
    }

    def "test null keys are after normal keys, but before null elements"() {
        given:
        def person1 = new Person("Alice","Adams", 30)
        def person2 = new Person("Charlie", "Clark", 35)
        def person3 = new Person(null, "Evans", 28)
        def person4 = new Person(null, "Brown", 25)
        def person5 = new Person("David", "Davis", 40)

        def elements = [person2, person1, null, person3, null, person4, null, person5]
        def keys = ["unknown1", "Alice", "unknown2", "Charlie", "unknown3"]
        def keyExtractor = { Person p -> p != null ? p.name() : null }

        when:
        def result = SortingUtils.sort(elements, keys, keyExtractor)

        then:
        result == [person1, person2, person3, person4, person5, null, null, null]
    }

    def "test complex examples with all cases"() {
        given:
        def people = samplePeopleComplex()

        def elements = [people[7], people[3], people[1], null, people[0], people[5], people[4], people[2], null, people[6]]

        def keys = ["unknown1", "first1", "unknown2", null, "unknown3", "first2", "unknown4"]
        def keyExtractor = { Person p -> p != null ? p.name() : null }
        def sameKeyComparator = Comparator.comparing({ Person p -> p.lastName() })

        when:
        def result = SortingUtils.sort(elements, keys, keyExtractor, sameKeyComparator)

        then:
        result.size() == people.size()
        result == people
    }

    def "test all list orders should return the same result after sorting"() {
        given:
        def people = samplePeople()
        def keys = ["unknown1", "first1", "unknown2",  null, "unknown3"]
        def keyExtractor = { Person p -> p != null ? p.name() : null }
        def sameKeyComparator = Comparator.comparing({ Person p -> p.lastName() })

        when:
        def result = SortingUtils.sort(elements, keys, keyExtractor, sameKeyComparator)

        then:
        result.size() == people.size()
        result == people

        where:
        elements << generateCases(samplePeople())
    }

    private static List<List<Person>> generateCases(List<Person> people) {
        Map<Integer, Person> peopleMap = people.withIndex().collectEntries { p, idx -> [(idx): p] }
        def constraintPairs = [[1, 2]]
        def result = []

        def valid = { List<Person> permutation ->
            Map<Person, Integer> permutationMap = permutation.withIndex().collectEntries { p, idx -> [(p): idx] }
            constraintPairs.every {constrain ->
                def first = peopleMap[constrain[0]]
                def second = peopleMap[constrain[1]]
                def newFirstIndex = permutationMap[first]
                def newSecondIndex = permutationMap[second]
                return newFirstIndex <= newSecondIndex
            }
        }
        people.permutations { perm ->
            if (valid(perm)) {
                result.add(perm)
            }
        }
        result
    }

    private static List<Person> samplePeople() {
        [
                new Person("first1", "last1", 30),
                new Person("first1", "last2", 25),
                new Person("first1", "last2", 35),
                new Person(null, "last1", 40),
                new Person("first2", "last1", 28),
                null
        ]
    }

    private static List<Person> samplePeopleComplex() {
        [
                new Person("first1", "last1", 30),
                new Person("first1", "last2", 25),
                new Person("first1", "last2", 35),
                new Person(null, "last1", 40),
                new Person(null, "last1", 33),
                new Person("first2", "last1", 28),
                new Person("first2", "last1", 45),
                new Person("first3", "last1", 22),
                null, null
        ]
    }
}
