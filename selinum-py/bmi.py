import unittest
import time
import tempfile
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

class BmiCalculatorTest(unittest.TestCase):

    def setUp(self):
        options = Options()
        options.binary_location = '/Applications/Brave Browser.app/Contents/MacOS/Brave Browser'
        
        # Temporary directory for Brave profile, mimicking the Java implementation
        tmpdir = tempfile.gettempdir()
        millis = int(time.time() * 1000)
        options.add_argument(f"--user-data-dir={tmpdir}/brave-profile-{millis}")
        options.add_argument("--remote-allow-origins=*")
        options.add_experimental_option('excludeSwitches', ['test-type'])
        
        self.driver = webdriver.Chrome(options=options)
        self.driver.maximize_window()
        self.wait = WebDriverWait(self.driver, 10)

    def test_bmi_calculator_with_metric_input(self):
        # =========================
        # Arrange
        # =========================
        age = 23
        gender = "m"
        height_cm = 183
        weight_kg = 103

        self.driver.get("https://www.calculator.net/bmi-calculator.html?ctype=metric")

        # =========================
        # Input Age
        # =========================
        age_input = self.wait.until(
            EC.element_to_be_clickable((By.NAME, "cage"))
        )
        age_input.clear()
        age_input.send_keys(str(age))

        # =========================
        # Select Gender
        # =========================
        gender_locator = (By.CSS_SELECTOR, f"input[name='csex'][value='{gender}']")
        
        gender_radio = self.wait.until(
            EC.presence_of_element_located(gender_locator)
        )

        if not gender_radio.is_selected():
            self.driver.execute_script("arguments[0].click();", gender_radio)

        self.assertTrue(gender_radio.is_selected(), "ไม่สามารถเลือก Male ได้")

        # =========================
        # Input Height
        # =========================
        height_input = self.wait.until(
            EC.element_to_be_clickable((By.NAME, "cheightmeter"))
        )
        height_input.clear()
        height_input.send_keys(str(height_cm))

        # =========================
        # Input Weight
        # =========================
        weight_input = self.wait.until(
            EC.element_to_be_clickable((By.NAME, "ckg"))
        )
        weight_input.clear()
        weight_input.send_keys(str(weight_kg))

        # =========================
        # Click Calculate
        # =========================
        calculate_button = self.wait.until(
            EC.element_to_be_clickable((By.CSS_SELECTOR, "input[type='submit'][value='Calculate']"))
        )
        calculate_button.click()

        # =========================
        # Expected BMI
        # =========================
        height_meter = height_cm / 100.0
        expected_bmi = weight_kg / (height_meter ** 2)
        expected_bmi_text = f"{expected_bmi:.1f}"

        # =========================
        # Wait Result
        # =========================
        body = (By.TAG_NAME, "body")
        
        self.wait.until(
            EC.text_to_be_present_in_element(body, f"BMI = {expected_bmi_text}")
        )
        
        page_text = self.driver.find_element(*body).text

        # =========================
        # Verify BMI
        # =========================
        self.assertTrue(
            f"BMI = {expected_bmi_text}" in page_text,
            "BMI ไม่ถูกต้อง"
        )

        # =========================
        # Verify Category
        # =========================
        if expected_bmi < 16.0:
            expected_category = "Severe Thinness"
        elif expected_bmi < 17.0:
            expected_category = "Moderate Thinness"
        elif expected_bmi < 18.5:
            expected_category = "Mild Thinness"
        elif expected_bmi < 25.0:
            expected_category = "Normal"
        elif expected_bmi < 30.0:
            expected_category = "Overweight"
        elif expected_bmi < 35.0:
            expected_category = "Obese Class I"
        elif expected_bmi < 40.0:
            expected_category = "Obese Class II"
        else:
            expected_category = "Obese Class III"

        self.assertTrue(
            f"({expected_category})" in page_text,
            f"Category ไม่ใช่ {expected_category}"
        )

        print("--------------------------------")
        print("Browser : Brave")
        print(f"Age     : {age}")
        print(f"Height  : {height_cm}")
        print(f"Weight  : {weight_kg}")
        print(f"BMI     : {expected_bmi_text}")
        print("Status  : TEST PASSED")
        print("--------------------------------")

    def tearDown(self):
        if self.driver:
            self.driver.quit()

if __name__ == "__main__":
    unittest.main()