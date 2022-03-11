package scraping

import java.util.concurrent.Callable

trait Task extends Callable[Unit]
