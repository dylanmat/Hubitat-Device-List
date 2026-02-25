import groovy.transform.Field

@Field static final String APP_VERSION = "0.1.2"

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
        section("Filters") {
            input "deviceTypeFilter", "enum", title: "Device Type", options: ["Any"] + getDeviceTypes(), defaultValue: "Any", required: true
            input "roomFilter", "enum", title: "Room", options: ["Any"] + getRooms(), defaultValue: "Any", required: true
            input "protocolFilter", "enum", title: "Protocol", options: ["Any", "Zigbee", "Z-Wave"], defaultValue: "Any", required: true
        }
        section("Device List") {
            List<Map> rows = getFilteredDevices().collect { device ->
                [id: device.id, name: device.displayName, type: getDeviceType(device), room: getDeviceRoom(device)]
            }
            paragraph rows ? rows.collect { "ID: ${it.id} | Name: ${it.name} | Type: ${it.type} | Room: ${it.room}" }.join("\n") : "No devices match the selected filters."
        }
    }
}

def installed() { initialize() }
def updated() { initialize() }
def initialize() {}

private List getFilteredDevices() {
    allDevices().findAll { device ->
        (deviceTypeFilter == "Any" || getDeviceType(device) == deviceTypeFilter) &&
        (roomFilter == "Any" || getDeviceRoom(device) == roomFilter) &&
        (protocolFilter == "Any" || getProtocol(device) == protocolFilter)
    }.sort { a, b -> a.displayName <=> b.displayName }
}

private List<String> getDeviceTypes() {
    allDevices().collect { getDeviceType(it) }.findAll { it }.unique().sort()
}

private List<String> getRooms() {
    allDevices().collect { getDeviceRoom(it) }.findAll { it }.unique().sort()
}

private List allDevices() {
    try {
        return getAllDevices() ?: []
    } catch (ignored) {
        return []
    }
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
