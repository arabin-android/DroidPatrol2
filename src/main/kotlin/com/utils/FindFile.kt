package com.utils

import java.io.File
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicLong


/**
 * @author Arabin
 * @date 03/20/2023
 * An utility class to find file in windows PC
 * */
class FindFile(private val filename: String, private val baseDir: File, private val concurrency: Int) {


    private val count: AtomicLong = AtomicLong(0)
    companion object {
        private val POISONPILL = File("")
    }

    private class RunnableDirSearch(
        private val dirQueue: BlockingQueue<File>,
        private val fileQueue: BlockingQueue<File>,
        private val count: AtomicLong,
        private val num: Int
    ) : Runnable {
        override fun run() {
            try {
                var dir = dirQueue.take()
                while (dir !== POISONPILL) {
                    val fi = dir.listFiles()
                    if (fi != null) {
                        for (element in fi) {
                            if (element.isDirectory) {
                                count.incrementAndGet()
                                dirQueue.put(element)
                            } else {
                                fileQueue.put(element)
                            }
                        }
                    }
                    val c = count.decrementAndGet()
                    if (c == 0L) {
                        end()
                    }
                    dir = dirQueue.take()
                }
            } catch (ie: InterruptedException) {
                // file found or error
            }
        }

        private fun end() {
            try {
                fileQueue.put(POISONPILL)
            } catch (e: InterruptedException) {
                // empty
            }
            for (i in 0 until num) {
                try {
                    dirQueue.put(POISONPILL)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private class CallableFileSearch(
        private val dirQueue: BlockingQueue<File>,
        private val fileQueue: BlockingQueue<File>,
        private val name: String,
        private val num: Int
    ) : Callable<File?> {
        @Throws(Exception::class)
        override fun call(): File? {
            var file = fileQueue.take()
            while (file !== POISONPILL) {
                val filename = file.name.lowercase(Locale.getDefault())
                val lf = name.lowercase(Locale.getDefault())
                if (filename.equals(name, ignoreCase = true) || filename.startsWith(lf) || filename.endsWith(lf)) {
                    end()
                    return file
                }
                file = fileQueue.take()
            }
            return null
        }

        private fun end() {
            for (i in 0 until num) {
                try {
                    dirQueue.put(POISONPILL)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }


    fun find(): File? {
        val ex = Executors.newFixedThreadPool(concurrency + 1)
        val dirQueue: BlockingQueue<File> = LinkedBlockingQueue()
        val fileQueue: BlockingQueue<File> = LinkedBlockingQueue(10000)
        for (i in 0 until concurrency) {
            ex.submit(RunnableDirSearch(dirQueue, fileQueue, count, concurrency))
        }
        count.incrementAndGet()
        dirQueue.add(baseDir)
        val c = ex.submit(CallableFileSearch(dirQueue, fileQueue, filename, concurrency))
        return try {
            c.get()
        } catch (e: Exception) {
            null
        } finally {
            ex.shutdownNow()
        }
    }

}