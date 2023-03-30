package dev.hotwire.strada

data class Message(
    /**
     * A unique identifier for this message. You can reply to messages by sending
     * the same message back, or creating a new message with the same id
     */
    val id: String,

    /**
     * The component the message is sent from (e.g. - "form", "page", etc)
     */
    val component: String,

    /**
     * The event that this message is about: "submit", "display", "send"
     */
    val event: String,

    /**
     * Data, represented in a json object string, to send along with the message.
     * For a "page" component, this might be `{"title": "Page Title"}`
     */
    val jsonData: String
) {
    /**
     * Convenience method for creating a new message from an existing message,
     * replacing its `event` and/or `jsonData`. The new message can be sent
     * back to the over the bridge to notify the web of a change/action.
     */
    fun replacing(
        event: String = this.event,
        jsonData: String = this.jsonData
    ) = Message(
        id = this.id,
        component = this.component,
        event = event,
        jsonData = jsonData
    )
}
