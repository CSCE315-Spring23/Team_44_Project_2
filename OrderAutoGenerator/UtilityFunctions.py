from datetime import date
from datetime import datetime
import datetime
import random


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
    pass


def numItemsForOrder() -> int:
    '''generates a random number of items for an order'''
    pass


def selectSoldItems(weightedItems: dict, numItems: int) -> list:
    '''chooses numItems number of items for an order based on the weighted menu'''
    pass


def selectItem(weightedItems: dict) -> int:
    '''chooses one item from the weighted menu'''
    pass


def getItemPrice(id: int, menuItems: dict) -> float:
    '''gets the price of an item'''
    pass


def createOrder(soldItems: list, orderID: int, curDate: date) -> list:
    '''creates an order from the list of sold items. -> [ID, Customer name, Total Cost, Date Ordered, Employee ID]'''
    pass


def createStringOfSoldItems(soldItems: list, menuItems: dict, orderID: int) -> str:
    '''turns a list of sold items into a series of lines for csv file '''
    pass

def createStringofSoldItem(itemID: int, menuItems: dict) -> str:
    '''turns one sold item into a line for the csv output -> [ID, MenuID, OrderID]'''
    pass


def createStringOfOrder(orderList: list) -> str:
    pass


def openMenu(fileName: str) -> dict:
    pass


def createMenuWeights(menuItems: dict) -> dict:
    pass


def isGameDay(date_to_check: date, gameDays: set) -> bool:
    pass


def parseGameDaysInput(days_string: str) -> set:
    pass
