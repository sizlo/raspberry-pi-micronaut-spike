package com.timsummertonbrier.database

import com.timsummertonbrier.authors.AuthorRepository
import com.timsummertonbrier.authors.AuthorRequest
import com.timsummertonbrier.books.BookRepository
import com.timsummertonbrier.books.BookRequest
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton

interface DefaultDataInserter {
    fun insertDefaultData()
}

@Singleton
@Requires(notEnv = ["raspberrypi"])
class DevDefaultDataInserter(
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository
) : DefaultDataInserter {
    override fun insertDefaultData() {
        if (authorRepository.getAllAuthors().isNotEmpty() || bookRepository.getAllBooks().isNotEmpty()) {
            return
        }

        val jrrTolkeinId = authorRepository.addAuthor(AuthorRequest("JRR", "Tolkein"))
        val richardOsmanId = authorRepository.addAuthor(AuthorRequest("Richard", "Osman"))
        val csLewisId = authorRepository.addAuthor(AuthorRequest("CS", "Lewis"))
        val stephenKingId = authorRepository.addAuthor(AuthorRequest("Stephen", "King"))
        val darrenShanId = authorRepository.addAuthor(AuthorRequest("Darren", "Shan"))

        bookRepository.addBooks(listOf(
            BookRequest("Fellowship of the Ring", "Fantasy", jrrTolkeinId),
            BookRequest("The Two Towers", "Fantasy", jrrTolkeinId),
            BookRequest("Return of the King", "Fantasy", jrrTolkeinId),
            BookRequest("The Hobbit", "Childrens", jrrTolkeinId),
            BookRequest("The Thursday Murder Club", "Mystery", richardOsmanId),
            BookRequest("The Man Who Died Twice", "Mystery", richardOsmanId),
            BookRequest("The Bullet That Missed", "Mystery", richardOsmanId),
            BookRequest("The Last Devil to Die", "Mystery", richardOsmanId),
            BookRequest("The Lion the Witch and the Wardrobe", "Childrens", csLewisId),
            BookRequest("The Pilgrims Regress", "Fantasy", csLewisId),
            BookRequest("It", "Horror", stephenKingId),
            BookRequest("The Shining", "Horror", stephenKingId),
            BookRequest("Cirque du Freak", "Childrens", darrenShanId),
            BookRequest("The Vampires Assistant", "Childrens", darrenShanId),
            BookRequest("Tunnels of Blood", "Childrens", darrenShanId),
            BookRequest("Vampire Mountain", "Childrens", darrenShanId),
            BookRequest("Trials of Death", "Childrens", darrenShanId),
            BookRequest("The Vampire Prince", "Childrens", darrenShanId),
            BookRequest("Hunters of the Dusk", "Childrens", darrenShanId),
            BookRequest("Allies of the Night", "Childrens", darrenShanId),
            BookRequest("Killers of the Dawn", "Childrens", darrenShanId),
            BookRequest("The Lake of Souls", "Childrens", darrenShanId),
            BookRequest("Lord of the Shadows", "Childrens", darrenShanId),
            BookRequest("Sons of Destiny", "Childrens", darrenShanId),
            BookRequest("Lord Loss", "Horror", darrenShanId),
            BookRequest("Demon Thief", "Horror", darrenShanId),
            BookRequest("Slawter", "Horror", darrenShanId),
            BookRequest("Bec", "Horror", darrenShanId),
            BookRequest("Blood Beast", "Horror", darrenShanId),
            BookRequest("Demon Apocalypse", "Horror", darrenShanId),
            BookRequest("Deaths Shadow", "Horror", darrenShanId),
            BookRequest("Wolf Island", "Horror", darrenShanId),
            BookRequest("Dark Calling", "Horror", darrenShanId),
            BookRequest("Hells Heroes", "Horror", darrenShanId),
        ))
    }
}

@Singleton
@Requires(env = ["raspberrypi"])
class NoopDefaultDataInserter : DefaultDataInserter {
    override fun insertDefaultData() {}
}