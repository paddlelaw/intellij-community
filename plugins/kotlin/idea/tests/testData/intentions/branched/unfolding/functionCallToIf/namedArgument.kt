fun test(b: Boolean, x: String, y: String) {
    <caret>println(s = if (b) x else y)
}

fun println(s: String) {}