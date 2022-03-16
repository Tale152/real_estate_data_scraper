package scraping

import java.util.concurrent.Callable

/**
 * Trait that define a task.
 */
trait Task extends Callable[Unit]
