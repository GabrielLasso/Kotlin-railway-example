sealed class Either<out L, out R> {
    data class Left<L>(val value: L): Either<L, Nothing>()
    data class Right<R>(val value: R): Either<Nothing, R>()
}

typealias Arrow<A,B> = (A) -> B

infix fun<A,B,C> Arrow<A,B>.º(f: Arrow<B,C>): Arrow<A,C> {
    return {
        f(this(it))
    }
}

data class Person(val name: String, val age: Int)
data class Response(val body: String, val code: Int)

fun validate(data: Either<Throwable, Person>): Either<Throwable, Person> {
    if (data is Either.Left) {
        return data
    }
    data as Either.Right

    if (data.value.age < 0) {
        return Either.Left(Exception("Age must be non negative"))
    }

    return data
}

fun save(data: Either<Throwable, Person>): Either<Throwable, Person> {
    if (data is Either.Left) {
        return data
    }

    println("Saved $data")
    return data
}

fun notify(data: Either<Throwable, Person>): Either<Throwable, Person> {
    if (data is Either.Left) {
        return data
    }

    println("Notified user $data")
    return data
}

fun buildResponse(data: Either<Throwable, Person>): Either<Throwable, Response> {
    if (data is Either.Left) {
        return data
    }

    return Either.Right(Response("Ok: $data", 200))
}

fun main() {
    val response = (::validate º ::save º ::notify º ::buildResponse)(Either.Right(Person("Gabriel", 26)))
    val invalidResponse = (::validate º ::save º ::notify º ::buildResponse)(Either.Right(Person("Invalid age", -3)))

    println(response)
    println(invalidResponse)
}