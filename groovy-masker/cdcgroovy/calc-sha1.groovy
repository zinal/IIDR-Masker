
def invoke(String scriptName, Object value) {
   if (value == null)
     return null;
   return "sha1:" + value.toString().digest("SHA-1");
}
