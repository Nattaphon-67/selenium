from selenium import webdriver
from selenium.webdriver.chrome.options import Options

options = Options()
options.binary_location = '/Applications/Brave Browser.app/Contents/MacOS/Brave Browser'
options.add_experimental_option('excludeSwitches', ['test-type'])

driver = webdriver.Chrome(options=options)

driver.get("http://www.google.com")

print(driver.title)

driver.quit()