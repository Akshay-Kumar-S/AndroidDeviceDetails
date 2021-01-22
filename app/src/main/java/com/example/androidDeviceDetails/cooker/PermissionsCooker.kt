
class PermissionsCooker : BaseCooker() {

    @Suppress("UNCHECKED_CAST")
    override fun <T> cook(time: TimePeriod, callback: ICookingDone<T>) {
            val permissionList = arrayListOf<String>()
        permissionList.addAll(listOf("Phone","Call Logs","Contacts","SMS",
                "Location",
                "Camera","Microphone","Storage","Calender","Body Sensors","Physical Activity"))
            callback.onDone(permissionList as ArrayList<T>)
    }
}