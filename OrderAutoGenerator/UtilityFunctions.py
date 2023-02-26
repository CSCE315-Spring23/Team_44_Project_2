from datetime import date
from datetime import datetime
import datetime
import random
import numpy as np
import math


def incrementDate(curDate: date) -> date:
    '''increments the date we are generating orders for by 1 day'''
    curDate = curDate + datetime.timedelta(days=1)
    return curDate


def isSunday(curDate: date) -> bool:
    '''returns true if the current date is a sunday'''
    return curDate.weekday() == 6


def getCustomerName(customers: list) -> str:
    '''generates a randome customer name'''
    return random.choice(customers)


def generateEmployeesList(fileName: str) -> list:
    '''creates a list of employee IDs from specified file'''
    employeesFile = open(fileName)
    if employeesFile.closed:
        raise Exception(f"Couldn't Open {fileName}")

    lines = employeesFile.readlines()
    IDs = []
    for line in lines:
        id = line.split()[-1]
        IDs.append(id)
    return IDs


def getEmployeeID(employeeIDs: list) -> str:
    '''chooses a random employee id'''
    return random.choice(employeeIDs)


def numOrdersForDay(day: date, gameDays: set) -> int:
    '''chooses a number of orders for a given day. Takes into account if its a gameday'''
    mean = 300
    stdev = 50
    value = np.random.normal(mean, stdev)
    value = math.ceil(value)
    return value


def numItemsForOrder() -> int:
    '''generates a random number of items for an order'''
    mean = 3
    stdev = 1
    value = np.random.norma(mean, stdev)
    value = abs(math.ceil(value))
    return value


def selectSoldItems(weightedItems: dict, numItems: int) -> list:
    '''chooses numItems number of items for an order based on the weighted menu'''
    items = []
    for i in range(numItems):
        item = selectItem(weightedItems)
        items.append(item)
    return items


def selectItem(weightedItems: dict) -> int:
    '''chooses one item from the weighted menu'''
    weights = weightedItems.values()
    keys = weightedItems.keys()
    return random.choice(keys, weights)


def getItemPrice(id: int, menuItems: dict) -> float:
    '''gets the price of an item'''
    itemPrice = float(menuItems[id][1])
    return itemPrice


def createOrder(soldItems: list, orderID: int, curDate: date, customer: str, menuItems: dict, employees: list) -> list:
    '''creates an order from the list of sold items. -> [ID, Customer name, Total Cost, Date Ordered, Employee ID]'''
    order = []
    order.append(orderID)
    order.append(customer)
    price = 0
    for item in soldItems:
        price += getItemPrice(item, menuItems)
    order.append(price)

    dateFormat = "%Y-%M-%D"
    order.append(curDate.strftime(dateFormat))
    order.append(getEmployeeID(employees))
    return order
    


def createStringOfSoldItems(soldItems: list, menuItems: dict, orderID: int, soldID: int) -> str:
    '''turns a list of sold items into a series of lines for csv file '''
    stringOfItems = ""
    for item in soldItems:
       stringOfItems += createStringofSoldItem(item, menuItems, orderID, soldID)
       soldID += 1
    return stringOfItems

def createStringofSoldItem(itemID: int, menuItems: dict, orderID: int, soldID: int) -> str:
    '''turns one sold item into a line for the csv output -> [ID, MenuID, OrderID]'''
    itemString = ""
    itemString += soldID + "," + itemID + "," + orderID
    return itemString


def createStringOfOrder(orderList: list) -> str:
    '''creates a string of an order from a list [ID, Customer name, Total Cost, Date Ordered, Employee ID] of the order in the format comma separated'''
    orderString = ",".join(orderList)
    return orderString


def openMenu(fileName: str) -> dict:
    menuFile = open(fileName)
    #get menu lines and skip first 2 lines
    menuLines = menuFile.readlines()[2:]
    menuFile.close()

    menu = {}

    for line in menuLines:
        lineList = line.split()
        id = int(lineList[0])
        menu[id] = lineList[1:]
    
    return menu


def createMenuWeights(menuItems: dict) -> dict:
    weightedMenu = {}

    for item in menuItems:
        weightedMenu[item] = menuItems[item][-1]
    return weightedMenu


def isGameDay(dateToCheck: date, gameDays: set) -> bool:
    dateFormat = "%Y-%M-%D"
    dateString = dateToCheck.strftime(dateFormat)
    return dateString in gameDays


def parseGameDaysInput(daysString: str) -> set:
    gameDaysList = daysString.split(",")
    gameDays = set()
    for day in gameDaysList:
        gameDays.add(day)
    return gameDays
