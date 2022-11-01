package br.com.erudio.services

import br.com.erudio.controller.PersonController
import br.com.erudio.data.vo.v1.PersonVO
import br.com.erudio.exceptions.ResourceNotFoundException
import br.com.erudio.mapper.DozerMapper
import br.com.erudio.mapper.PersonMapper
import br.com.erudio.model.Person
import br.com.erudio.repository.PersonRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import br.com.erudio.data.vo.v2.PersonVO as PersonV2

import org.springframework.stereotype.Service

import java.util.logging.Logger

@Service
class PersonService {
    @Autowired
    private lateinit var repository: PersonRepository

    @Autowired
    private lateinit var mapper: PersonMapper
    private val logger = Logger.getLogger(PersonService::class.java.name)

    fun finById(id: Long): PersonVO {
        logger.info("Finding one person with ID $id" )



        var person = repository.findById(id)
            .orElseThrow { ResourceNotFoundException("No records found this ID!") }
        val personVO: PersonVO = DozerMapper.parseObject(person, PersonVO::class.java)
        val withSelfRel = linkTo(PersonController::class.java).slash(personVO.key).withSelfRel()
        personVO.add(withSelfRel)
        return personVO

    }

    fun findAll(): List<PersonVO> {
        logger.info("Finding a list of persons")


      val persons =  repository.findAll()
        return DozerMapper.parseListObjects(persons, PersonVO::class.java)
    }


    fun create(person: PersonVO): PersonVO {
        logger.info("Creating one person with name ${person.firstName}!")
        var entity: Person = DozerMapper.parseObject(person, Person::class.java)
        val personVO: PersonVO =  DozerMapper.parseObject(repository.save(entity), PersonVO::class.java)
        val withSelfRel = linkTo(PersonController::class.java).slash(personVO.key).withSelfRel()
        personVO.add(withSelfRel)
        return personVO
    }
    fun createV2(person: PersonV2): PersonV2 {
        logger.info("Creating one person with name ${person.firstName}!")
        var entity: Person = mapper.mapVOToEntity(person)
        val personV2: PersonV2 =  DozerMapper.parseObject(repository.save(entity), PersonV2::class.java)
        val withSelfRel = linkTo(PersonController::class.java).slash(personV2.key).withSelfRel()
        personV2.add(withSelfRel)
        return personV2
    }

    fun update(person: PersonVO) : PersonVO {
        logger.info("Updating a person with ID  ${person.key}!")
        val entity = repository.findById(person.key)
            .orElseThrow { ResourceNotFoundException("No records found this ID!") }
        entity.firstName = person.firstName
        entity.lastName = person.lastName
        entity.address = person.address
        entity.gender = person.gender
        val personVO: PersonVO =  DozerMapper.parseObject(repository.save(entity), PersonVO::class.java)
        val withSelfRel = linkTo(PersonController::class.java).slash(personVO.key).withSelfRel()
        personVO.add(withSelfRel)
        return personVO
    }

    fun delete(id: Long) {
        logger.info("Deleting a person with id ${id}!")
        val entity = repository.findById(id)
            .orElseThrow {
                ResourceNotFoundException("No records found this ID!")
            }
        repository.delete(entity)
    }

}