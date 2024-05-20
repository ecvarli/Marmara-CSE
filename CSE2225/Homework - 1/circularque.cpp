#include <stdio.h>
#include <stdlib.h>
#include <time.h>
typedef struct CircularQueue
{
   int rear, front;
   int size;
   int data[50];
}CircularQueue;

int isFull(CircularQueue * q) {
  if ((q->front == q->rear + 1) || (q->front == 0 && q->rear == q->size - 1)) return 1;
  return 0;
}

int isEmpty(CircularQueue * q) {
  if (q->front == -1) return 1;
  return 0;
}

void put(CircularQueue* q,int newData){
     if (isFull(q)){
        return;
     }
    else {
        if (q->front == -1){
            q->front = 0;
        }
        q->rear = (q->rear + 1) % q->size;
        q->data[q->rear] = newData;
    }
}

int pop(CircularQueue* q)
{
   int item;
    if (isEmpty(q)) {
        return (-1);
    } else {
    item = q->data[q->front];
    if (q->front == q->rear) {
      q->front = -1;
      q->rear = -1;
    } 
    else {
      q->front = (q->front + 1) % q->size;
    }
    return item;
  }
}
 



int main() {
    srand(time(NULL));

    CircularQueue queue;
    queue.front = -1;
    queue.rear = -1;
    queue.size = 50;
    int queueSize = queue.size;


    // fill queue
    for(int i = 0 ; i < queueSize;i++){
        put(&queue,rand() % queueSize);
    }

    printf("Front / Rear: %d , %d \n",queue.front,queue.rear);

    // dequeue half of the queue

    for(int i = 0 ; i < queueSize / 2;i++){
        pop(&queue);
    }
    
    printf("Front / Rear: %d , %d \n",queue.front,queue.rear);

    // fill half again
    for(int i = 0 ; i < queueSize / 2;i++){
         put(&queue,rand() % queueSize);
    }

     printf("Front / Rear: %d , %d \n",queue.front,queue.rear);

    return 0;
}
