#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}

#end
import by.vkatz.katzilla.FragmentScreen
import by.vkatz.katzilla.helpers.KatzillaFragment

#parse("File Header.java")
class ${NAME} : KatzillaFragment<${NAME}.${Model_name}>() {
    class ${Model_name} : FragmentScreen.ScreenModel() {
       
    }
}
