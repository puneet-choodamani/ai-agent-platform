import { gql } from "@apollo/client";
import { useQuery, useMutation } from "@apollo/client/react";


const GET_TASKS = gql`
  query GetTasks {
    tasks {
      id
      title
      prompt
      status
    }
  }
`;

const CREATE_TASK = gql`
  mutation CreateTask($input: CreateTaskInput!) {
    createTask(input: $input) {
      id
      title
      prompt
      status
    }
  }
`;

type Task = {
  id: string;
  title: string;
  prompt: string;
  status: string;
};

function App() {
  const { data, loading, error } = useQuery<{ tasks: Task[] }>(GET_TASKS);
  const [createTask] = useMutation(CREATE_TASK, {
    refetchQueries: [GET_TASKS],
  });

  if (loading) {
    return <p>Loading tasks...</p>;
  }

  if (error) {
    return <p>Error: {error.message}</p>;
  }

  const tasks = data?.tasks ?? [];

  return (
    <div style={{ padding: "2rem" }}>
      <h1>AI Agent Platform</h1>

      <h2>Tasks</h2>

      {tasks.map((task) => (
        <div key={task.id}>
          <h3>{task.title}</h3>
          <p>{task.prompt}</p>
          <strong>{task.status}</strong>
        </div>
      ))}

      <button
        onClick={() =>
          createTask({
            variables: {
              input: {
                title: "Analyze GitHub repository",
                prompt: "Understand the repository architecture",
              },
            },
          })
        }
      >
        Create Task
      </button>
    </div>
  );
}

export default App;