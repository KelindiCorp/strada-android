package dev.hotwire.strada

import com.nhaarman.mockito_kotlin.*
import kotlinx.serialization.Serializable
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class BridgeComponentTest {
    private lateinit var component: TestData.OneBridgeComponent
    private val delegate: BridgeDelegate<TestData.AppBridgeDestination> = mock()

    private val message = Message(
        id = "1",
        component = "one",
        event = "connect",
        metadata = Metadata("https://37signals.com"),
        jsonData = """{"title":"Page-title","subtitle":"Page-subtitle"}"""
    )

    @Before
    fun setup() {
        Strada.config.jsonConverter = KotlinXJsonConverter()
        component = TestData.OneBridgeComponent("one", delegate)
    }

    @Test
    fun didReceive() {
        assertNull(component.receivedMessageForPublic("connect"))

        component.didReceive(message)
        assertEquals(message, component.receivedMessageForPublic("connect"))
    }

    @Test
    fun didStart() {
        assertEquals(false, component.onStartCalled)

        component.didStart()
        assertEquals(true, component.onStartCalled)
    }

    @Test
    fun didStop() {
        assertEquals(false, component.onStopCalled)

        component.didStop()
        assertEquals(true, component.onStopCalled)
    }

    @Test
    fun didReceiveSavesLastMessage() {
        val newJsonData = """{"title":"Page-title"}"""
        val newMessage = message.replacing(jsonData = newJsonData)

        component.didReceive(message)
        assertEquals(message, component.receivedMessageForPublic("connect"))

        component.didReceive(newMessage)
        assertEquals(newMessage, component.receivedMessageForPublic("connect"))
    }

    @Test
    fun replyWith() {
        val newJsonData = """{"title":"Page-title"}"""
        val newMessage = message.replacing(jsonData = newJsonData)

        component.replyWith(newMessage)
        verify(delegate).replyWith(eq(newMessage))
    }

    @Test
    fun replyTo() {
        component.didReceive(message)

        component.replyTo("connect")
        verify(delegate).replyWith(eq(message))
    }

    @Test
    fun replyToReplacingJsonData() {
        val newJsonData = """{"title":"Page-title"}"""
        val newMessage = message.replacing(jsonData = newJsonData)

        component.didReceive(message)

        component.replyTo("connect", newJsonData)
        verify(delegate).replyWith(eq(newMessage))
    }

    @Test
    fun replyToReplacingData() {
        val newJsonData = """{"title":"Page-title"}"""
        val newMessage = message.replacing(jsonData = newJsonData)

        component.didReceive(message)

        component.replyTo("connect", MessageData(title = "Page-title"))
        verify(delegate).replyWith(eq(newMessage))
    }

    @Test
    fun replyToReplacingDataWithNoConverter() {
        Strada.config.jsonConverter = null

        component.didReceive(message)

        assertThatThrownBy { component.replyTo("connect", MessageData(title = "Page-title")) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage(StradaJsonConverter.NO_CONVERTER)
    }

    @Test
    fun replyToIgnoresNotReceived() {
        component.replyTo("connect")
        verify(delegate, never()).replyWith(any())
    }

    @Test
    fun replyToReplacingJsonDataIgnoresNotReceived() {
        val newJsonData = """{"title":"Page-title"}"""

        component.replyTo("connect", newJsonData)
        verify(delegate, never()).replyWith(any())
    }

    @Serializable
    private class MessageData(val title: String)
}
