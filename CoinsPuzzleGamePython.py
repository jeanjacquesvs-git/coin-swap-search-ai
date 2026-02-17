#import heap for heap lfunctionality later
import heapq

#initialise State class with all its attributes
class State:
    def __init__(self,board,parent=None,gValue=0):
        self.board=board.copy()
        self.parent= parent
        self.gValue=gValue
        self.hValue=self.calculate_heuristic()
        self.fValue= self.gValue+self.hValue

    #function that calculates the heuristic -- how far it is from the goal state
    def calculate_heuristic(self):
            goalState=['B','B','B','0','R','R','R']
            hValue=0
            for i in range(7):
                 if self.board[i]!=goalState[i]:
                    hValue+=1
            return hValue
        
    #finding the empty cell
    def find_empty(self):
        for i in range(7):
              if self.board[i]=='0':
                   return i
              
        return -1
    
    def generate_children(self):
        children=[]
        zero=self.find_empty()
        moves=[-1,1,-2,2]

        for move in moves:
            #checks if moves are valid
            from_pos = zero + move
            if 0 <= from_pos < 7 and self.board[from_pos] !='0':
                #copy board
                new_board=self.board.copy()

                #the coin moves to the spot where 0 was
                new_board[zero]=new_board[from_pos]
                
                #the old position is now filled with a 0
                new_board[from_pos] ='0'

                child= State(new_board,self,self.gValue+1)
                children.append(child)

        return children         
    
    #for priority queue
    def __lt__(self,other):
        return self.fValue <other.fValue
    
    def __eq__(self,other):
        return isinstance(other,State) and self.board ==other.board
    
    #for visited set
    def __hash__(self):
        return hash(tuple(self.board))
    
    def __str__(self):
        return f"g={self.gValue} h={self.hValue} f={self.fValue}\n{''.join(self.board)}"
    
# Reading the textfiles and stripping it from its spaces
def read_input(file_path):
    file = open(file_path,"r")
    line= file.readline().strip()
    tokens=line.split()
    file.close
    return State(tokens)

def solve_and_write_output(start_state, output_file, print_screen=True):
        open_list=[]
        heapq.heappush(open_list, start_state)
        best_g={tuple(start_state.board):0}

        goal_state =None
        while len(open_list)>0:
            current = heapq.heappop(open_list)

            #Goal Check
            if current.hValue==0:
                goal_state=current
                break

            #Expand children
            for child in current.generate_children():
                board_tuple=tuple(child.board)
                if board_tuple not in best_g or child.gValue < best_g[board_tuple]:
                    best_g[board_tuple] = child.gValue
                    heapq.heappush(open_list, child)

        if goal_state:
            write_solution(goal_state,output_file,print_screen)
        else:
            print("No solution found")



# This function prints and saves the final solution path
# It starts from the goal state and walks backwards using parent links
def write_solution(goal_state, output_file, print_screen=True):
    # will store all states from start → goal
    path = []                
    # start at the goal     
    current = goal_state          

    # Follow the parent pointers backwards until we reach the start (parent=None)
    while current != None:
        # add current state to the path
        path.append(current)      
        # move one step back
        current = current.parent  

    # we built it backwards, so flip it to start → goal
    path.reverse()                


    # Open output file to write the solution steps
    file = open(output_file, "w")

    # Print every step of the solution
    for state in path:
        # save to file
        file.write(str(state) + "\n")  
        if print_screen:
            # also show on screen
            print(state)               


    # Number of moves = number of states - 1
    # because the first state is the starting position
    moves = len(path) - 1

    # write total moves to file
    file.write(f"Total Moves: {moves}\n")  
    # close file
    file.close()                           

    if print_screen:
        # print total moves on screen
        print("Total Moves:", moves)       

# Program starts running here
if __name__ == "__main__":

    # Locations of the input files
    fileA = r"C:\Users\User\Desktop\stateA.txt"
    fileB = r"C:\Users\User\Desktop\stateB.txt"


    # Solve first puzzle
    # read starting board
    start = read_input(fileA)                   
    # solve + save answer
    solve_and_write_output(start, "outputA.txt", True)  


    # Solve second puzzle 
    # read second starting board
    start = read_input(fileB)               
    # solve + save answer    
    solve_and_write_output(start, "outputB.txt", True) 
