import groovy.transform.Field

@Field static final String APP_VERSION = "0.1.4"

definition(
    name: "Hubitat Device List",
    namespace: "hubitat.community",
    author: "Codex",
    description: "List Hubitat devices with simple filters.",
    category: "Convenience",
    singleInstance: true,
    installOnOpen: true,
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "",
    appVersion: APP_VERSION
)

preferences {
    page(name: "mainPage", title: "Hubitat Device List", install: true, uninstall: true)
}

def mainPage() {
    dynamicPage(name: "mainPage") {
        section("Device Access") {
            paragraph "Hubitat apps only see devices you authorize. Select one or more devices to populate filters and results."
            input "authorizedDevices", "capability.*", title: "Authorized Devices", multiple: true, required: false, submitOnChange: true
        }
        section("Filters") {
            input "deviceTypeFilter", "enum", title: "Device Type", options: ["Any"] + getDeviceTypes(), defaultValue: "Any", required: false, submitOnChange: true
            input "roomFilter", "enum", title: "Room", options: ["Any"] + getRooms(), defaultValue: "Any", required: false, submitOnChange: true
            input "protocolFilter", "enum", title: "Protocol", options: ["Any", "Zigbee", "Z-Wave"], defaultValue: "Any", required: false, submitOnChange: true
        }

        List<Map> rows = getFilteredDevices().collect { device ->
            [id: device.id, name: device.displayName, type: getDeviceType(device), room: getDeviceRoom(device)]
        }
        String copyText = rows.collect { "${it.id}\t${it.name}\t${it.type}\t${it.room}" }.join("\n")

        section("Device List") {
            paragraph rows ? rows.collect { "ID: ${it.id} | Name: ${it.name} | Type: ${it.type} | Room: ${it.room}" }.join("\n") : "No devices match the selected filters."
        }
        section("Copy/Paste Output") {
            paragraph "Columns: Device ID, Device Name, Type, Room"
            input "outputPreview", "textarea", title: "Filtered Device Rows", defaultValue: copyText, required: false
        }
    }
}

def installed() { initialize() }
def updated() { initialize() }
def initialize() {}

private List getFilteredDevices() {
    sourceDevices().findAll { device ->
        (deviceTypeFilter in [null, "Any"] || getDeviceType(device) == deviceTypeFilter) &&
        (roomFilter in [null, "Any"] || getDeviceRoom(device) == roomFilter) &&
        (protocolFilter in [null, "Any"] || getProtocol(device) == protocolFilter)
    }.sort { a, b -> a.displayName <=> b.displayName }
}

private List<String> getDeviceTypes() {
    sourceDevices().collect { getDeviceType(it) }.unique().sort()
}

private List<String> getRooms() {
    sourceDevices().collect { getDeviceRoom(it) }.unique().sort()
}

private List sourceDevices() {
    List selected = (authorizedDevices instanceof List) ? authorizedDevices : (authorizedDevices ? [authorizedDevices] : [])
    if (selected) return selected
    try { return getAllDevices() ?: [] } catch (ignored) { return [] }
}

private String getDeviceType(device) {
    device?.typeName ?: device?.capabilities?.collect { it.name }?.join(", ") ?: "Unknown"
}

private String getDeviceRoom(device) {
    def room = device?.metaClass?.respondsTo(device, "getRoomName") ? device.getRoomName() : device?.properties?.roomName
    room ?: "Unassigned"
}

private String getProtocol(device) {
    if (device?.hasProperty("zigbeeId") && device.zigbeeId) return "Zigbee"
    if (device?.hasProperty("zwaveNodeId") && device.zwaveNodeId != null) return "Z-Wave"
    "Other"
}
