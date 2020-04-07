
def invoke(String scriptName, Object value) {
   if (value == null)
     return null;
   return "md5:" + value.toString().md5();
}
